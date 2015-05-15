/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
package org.lwjgl.stb

import org.lwjgl.generator.*

val STB_PACKAGE = "org.lwjgl.stb"


// stb_image.h

val stbi_uc = typedef(unsigned_char, "stbi_uc")
val stbi_uc_p = PointerType(stbi_uc)

val STBIReadCallback = CallbackType(callback(
	STB_PACKAGE, int, "STBIReadCallback",
	"The {@code stbi_io_callbacks.read} callback.",
	void_p.IN("user", "a pointer to user data"),
	char_p.IN("data", "the data buffer to fill"),
	int.IN("size", "the number of bytes to read"),
	returnDoc = "the number of bytes actually read",
	samConstructor = "STBImage"
) {
	documentation = "Instances of this interface may be set to the {@code read} field of the ##STBIIOCallbacks struct."
}, "stbi_io_callbacks.read")

val STBISkipCallback = CallbackType(callback(
	STB_PACKAGE, int, "STBISkipCallback",
	"The {@code stbi_io_callbacks.skip} callback.",
	void_p.IN("user", "a pointer to user data"),
	int.IN("n", "the number of bytes to skip if positive, or <em>unget</em> the last {@code -n} bytes if negative"),
	samConstructor = "STBImage"
) {
	documentation = "Instances of this interface may be set to the {@code skip} field of the ##STBIIOCallbacks struct."
}, "stbi_io_callbacks.read")

val STBIEOFCallback = CallbackType(callback(
	STB_PACKAGE, int, "STBIEOFCallback",
	"The {@code stbi_io_callbacks.eof} callback.",
	void_p.IN("user", "a pointer to user data"),
	returnDoc = "nonzero if we are at the end of file/data",
	samConstructor = "STBImage"
) {
	documentation = "Instances of this interface may be set to the {@code eof} field of the ##STBIIOCallbacks struct."
}, "stbi_io_callbacks.read")

val stbi_io_callbacks = struct(STB_PACKAGE, "STBIIOCallbacks", structName = "stbi_io_callbacks") {
	documentation = "Image IO callbacks, used by STBImage##stb_load_from_callbacks()."
	nativeImport("stb_image.h")
	STBIReadCallback.member("read")
	STBISkipCallback.member("skip")
	STBIEOFCallback.member("eof")
}.nativeType
val stbi_io_callbacks_p = StructType(stbi_io_callbacks)

// stb_truetype.h

val stbtt_bakedchar = struct(STB_PACKAGE, "STBTTBakedChar", structName = "stbtt_bakedchar") {
	documentation = ""
	nativeImport("stb_truetype.h")
	unsigned_short.member("x0")
	unsigned_short.member("y0")
	unsigned_short.member("x1")
	unsigned_short.member("y1")
	float.member("xoff")
	float.member("yoff")
	float.member("xadvance")
}.nativeType
val stbtt_bakedchar_p = StructType(stbtt_bakedchar)

val stbtt_aligned_quad = struct(STB_PACKAGE, "STBTTAlignedQuad", structName = "stbtt_aligned_quad") {
	documentation = ""
	nativeImport("stb_truetype.h")
	float.member("x0")
	float.member("y0")
	float.member("s0")
	float.member("t0")
	float.member("x1")
	float.member("y1")
	float.member("s1")
	float.member("t1")
}.nativeType
val stbtt_aligned_quad_p = StructType(stbtt_aligned_quad)