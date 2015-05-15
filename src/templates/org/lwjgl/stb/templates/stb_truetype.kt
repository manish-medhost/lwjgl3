/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
package org.lwjgl.stb.templates

import org.lwjgl.generator.*
import org.lwjgl.stb.STB_PACKAGE
import org.lwjgl.stb.stbtt_aligned_quad_p
import org.lwjgl.stb.stbtt_bakedchar_p

val stb_truetype = "STBTruetype".nativeClass(packageName = STB_PACKAGE, prefix = "STBTT", prefixMethod = "stbtt_") {
	nativeDirective(
		"""#define STB_TRUETYPE_IMPLEMENTATION
#include "stb_truetype.h"""")

	documentation =
		"""
		Native bindings to stb_truetype.h from the <a href="https://github.com/nothings/stb">stb library</a>.

		This library processes TrueType files:
		${ul(
			"parse files",
			"extract glyph metrics",
			"extract glyph shapes",
			"render glyphs to one-channel bitmaps with antialiasing (box filter)"
		)}

		<h3>ADDITIONAL DOCUMENTATION</h3>
		Some important concepts to understand to use this library:

		<b>Codepoint</b>

		Characters are defined by unicode codepoints, e.g. 65 is uppercase A, 231 is lowercase c with a cedilla, 0x7e30 is the hiragana for "ma".

		<b>Glyph</b>

		A visual character shape (every codepoint is rendered as some glyph)

		<b>Glyph index</b>

		A font-specific integer ID representing a glyph

		<b>Baseline</b>

		Glyph shapes are defined relative to a baseline, which is the bottom of uppercase characters. Characters extend both above and below the baseline.

		<b>Current Point</b>

		As you draw text to the screen, you keep track of a "current point" which is the origin of each character. The current point's vertical position is the
		baseline. Even "baked fonts" use this model.

		<b>Vertical Font Metrics</b>

		The vertical qualities of the font, used to vertically position and space the characters. See docs for #GetFontVMetrics().

		<b>Font Size in Pixels or Points</b>

		The preferred interface for specifying font sizes in stb_truetype is to specify how tall the font's vertical extent should be in pixels. If that sounds
		good enough, skip the next paragraph.

		Most font APIs instead use "points", which are a common typographic measurement for describing font size, defined as 72 points per inch. stb_truetype
		provides a point API for compatibility. However, true "per inch" conventions don't make much sense on computer displays since different monitors have
		different number of pixels per inch. For example, Windows traditionally uses a convention that there are 96 pixels per inch, thus making 'inch'
		measurements have nothing to do with inches, and thus effectively defining a point to be 1.333 pixels. Additionally, the TrueType font data provides an
		explicit scale factor to scale a given font's glyphs to points, but the author has observed that this scale factor is often wrong for non-commercial
		fonts, thus making fonts scaled in points according to the TrueType spec incoherently sized in practice.

		<h3>ADVANCED USAGE</h3>
		
		Quality:
		${ul(
			"""
			Use the functions with Subpixel at the end to allow your characters to have subpixel positioning. Since the font is anti-aliased, not hinted, this
			is very important for quality. (This is not possible with baked fonts.)
			""",
			"Kerning is now supported, and if you're supporting subpixel rendering then kerning is worth using to give your text a polished look."
		)}
		Performance:
		${ul(
			"""
			Convert Unicode codepoints to glyph indexes and operate on the glyphs; if you don't do this, stb_truetype is forced to do the conversion on every
			call.
			""",
			"""
		    There are a lot of memory allocations. We should modify it to take a temp buffer and allocate from the temp buffer (without freeing), should help
		    performance a lot.
		    """
		)}

		<h3>NOTES</h3>

		The system uses the raw data found in the .ttf file without changing it and without building auxiliary data structures. This is a bit inefficient on
		little-endian systems (the data is big-endian), but assuming you're caching the bitmaps or glyph shapes this shouldn't be a big deal.

		It appears to be very hard to programmatically determine what font a given file is in a general way. I provide an API for this, but I don't recommend
		it.

		<h3>SAMPLE PROGRAMS</h3>

		Incomplete text-in-3d-api example, which draws quads properly aligned to be lossless:
		${codeBlock(
			"""
unsigned char ttf_buffer[1<<20];
unsigned char temp_bitmap[512*512];

stbtt_bakedchar cdata[96]; // ASCII 32..126 is 95 glyphs
GLuint ftex;

void my_stbtt_initfont(void)
{
   fread(ttf_buffer, 1, 1<<20, fopen("c:/windows/fonts/times.ttf", "rb"));
   stbtt_BakeFontBitmap(ttf_buffer,0, 32.0, temp_bitmap,512,512, 32,96, cdata); // no guarantee this fits!
   // can free ttf_buffer at this point
   glGenTextures(1, &ftex);
   glBindTexture(GL_TEXTURE_2D, ftex);
   glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, 512,512, 0, GL_ALPHA, GL_UNSIGNED_BYTE, temp_bitmap);
   // can free temp_bitmap at this point
   glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
}

void my_stbtt_print(float x, float y, char *text)
{
   // assume orthographic projection with units = screen pixels, origin at top left
   glEnable(GL_TEXTURE_2D);
   glBindTexture(GL_TEXTURE_2D, ftex);
   glBegin(GL_QUADS);
   while (*text) {
      if (*text >= 32 && *text < 128) {
         stbtt_aligned_quad q;
         stbtt_GetBakedQuad(cdata, 512,512, *text-32, &x,&y,&q,1);//1=opengl & d3d10+,0=d3d9
         glTexCoord2f(q.s0,q.t1); glVertex2f(q.x0,q.y0);
         glTexCoord2f(q.s1,q.t1); glVertex2f(q.x1,q.y0);
         glTexCoord2f(q.s1,q.t0); glVertex2f(q.x1,q.y1);
         glTexCoord2f(q.s0,q.t0); glVertex2f(q.x0,q.y1);
      }
      ++text;
   }
   glEnd();
}""")}
		Complete program (this compiles): get a single bitmap, print as ASCII art:
		${codeBlock(
			"""
char ttf_buffer[1<<25];

int main(int argc, char **argv)
{
   stbtt_fontinfo font;
   unsigned char *bitmap;
   int w,h,i,j,c = (argc > 1 ? atoi(argv[1]) : 'a'), s = (argc > 2 ? atoi(argv[2]) : 20);

   fread(ttf_buffer, 1, 1<<25, fopen(argc > 3 ? argv[3] : "c:/windows/fonts/arialbd.ttf", "rb"));

   stbtt_InitFont(&font, ttf_buffer, stbtt_GetFontOffsetForIndex(ttf_buffer,0));
   bitmap = stbtt_GetCodepointBitmap(&font, 0,stbtt_ScaleForPixelHeight(&font, s), c, &w, &h, 0,0);

   for (j=0; j < h; ++j) {
      for (i=0; i < w; ++i)
         putchar(" .:ioVM@"[bitmap[j*w+i]>>5]);
      putchar('\n');
   }
   return 0;
}""")}
		Complete program: print "Hello World!" banner, with bugs:
		${codeBlock(
			"""
char buffer[24<<20];
unsigned char screen[20][79];

int main(int arg, char **argv)
{
   stbtt_fontinfo font;
   int i,j,ascent,baseline,ch=0;
   float scale, xpos=2; // leave a little padding in case the character extends left
   char *text = "Heljo World!";

   fread(buffer, 1, 1000000, fopen("c:/windows/fonts/arialbd.ttf", "rb"));
   stbtt_InitFont(&font, buffer, 0);

   scale = stbtt_ScaleForPixelHeight(&font, 15);
   stbtt_GetFontVMetrics(&font, &ascent,0,0);
   baseline = (int) (ascent*scale);

   while (text[ch]) {
      int advance,lsb,x0,y0,x1,y1;
      float x_shift = xpos - (float) floor(xpos);
      stbtt_GetCodepointHMetrics(&font, text[ch], &advance, &lsb);
      stbtt_GetCodepointBitmapBoxSubpixel(&font, text[ch], scale,scale,x_shift,0, &x0,&y0,&x1,&y1);
      stbtt_MakeCodepointBitmapSubpixel(&font, &screen[baseline + y0][(int) xpos + x0], x1-x0,y1-y0, 79, scale,scale,x_shift,0, text[ch]);
      // note that this stomps the old data, so where character boxes overlap (e.g. 'lj') it's wrong
      // because this API is really for baking character bitmaps into textures. if you want to render
      // a sequence of characters, you really need to render each bitmap to a temp buffer, then
      // "alpha blend" that into the working buffer
      xpos += (advance * scale);
      if (text[ch+1])
         xpos += scale*stbtt_GetCodepointKernAdvance(&font, text[ch],text[ch+1]);
      ++ch;
   }

   for (j=0; j < 20; ++j) {
      for (i=0; i < 78; ++i)
         putchar(" .:ioVM@"[screen[j][i]>>5]);
      putchar('\n');
   }

   return 0;
}""")}
		"""

	int(
		"BakeFontBitmap",
		"Bakes a font to a bitmap for use as texture.",

		const _ unsigned_char_p.IN("data", "the font data"),
		Expression("0") _ int.IN("offset", "the font data offset, use 0 for plain .ttf files"),
		float.IN("pixel_height", "the font height, in pixels"),
		Check("pw * ph") _ unsigned_char_p.OUT("pixels", "a buffer in which to write the font bitmap"),
		int.IN("pw", "the bitmap width, in pixels"),
		int.IN("ph", "the bitmap height, in pixels"),
		int.IN("first_char", "the first character to bake"),
		AutoSize("chardata") _ int.IN("num_chars", "the number of characters to bake, starting at {@code first_char}"),
		stbtt_bakedchar_p.OUT("chardata", "an array of ##STBTTBakedChar structs, it's {@code num_chars} long"),

		returnDoc =
		"""
		if positive, the first unused row of the bitmap. If negative, returns the negative of the number of characters that fit. If 0, no characters fit and no
		rows were used.
		"""
	)

	void(
		"GetBakedQuad",
		"Computes quad to draw for a given char.",

		stbtt_bakedchar_p.IN("chardata", ""),
		int.IN("pw", "the bitmap width, in pixels"),
		int.IN("ph", "the bitmap height, in pixels"),
		int.IN("char_index", "the character index in the {@code chardata} array"),
		Check(1) _ float_p.OUT("xpos", "the x position"),
		Check(1) _ float_p.OUT("ypos", "the y position"),
		stbtt_aligned_quad_p.OUT("q", "an ##STBTTAlignedQuad struct"),
		int.IN("opengl_fillrule", "1=opengl &amp; d3d10+, 0=d3d9")
	)
}