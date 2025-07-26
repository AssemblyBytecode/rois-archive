; Rois Archive External Parser V1.1
; Usage: Include in your Blitz3D Applications to utilize Rois 
; Archives, eg. Portable executables.

; PLANNED features for V1.1 (Spoiler: Encryption wasn't added.)
; Encryption (Priority)
; Binary Support

Dim RoisArcBytesOut(1)

Function extractFromRoisArcFile(roisArcFile$, fileName$, key$)
	Local roisArchHandle = ReadFile(roisArcFile$)
	If Not roisArchHandle Then RuntimeError("Archive file does not exist or is inaccessible.")
	Local argRoisArch1$ = ""
	Local argRoisArch2 = 0
	Local cmdRoisArch$ = ""
	Local argsRoisArch$ = ""
	Local spacePosRoisArch = 0
	Local archKey$ = ""
	Local totalBase64String$ = ""
	Local entireFileData$ = ""
	While Not Eof(roisArchHandle)
		argRoisArch1$ = ""
		argRoisArch2 = 0
		cmdRoisArch$ = ""
		argsRoisArch$ = ""
		spacePosRoisArch = 0
		totalBase64String$ = ""
		Line$ = Trim(ReadLine$(roisArchHandle))
		
		; Skip empty lines using label. (Remnant from Rois Archive opener. Labels cannot exist in Functions)
		If Line$ = "" Then RuntimeError("External Parser is unable to parse some blank lines.")
		; Parse command and arguments
		spacePosRoisArch = Instr(Line$ + " ", " ")
		If spacePosRoisArch <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
		cmdRoisArch$ = Upper$(Left$(Line$, spacePosRoisArch - 1))
		argsRoisArch$ = Mid$(Line$, spacePosRoisArch + 1)
		
		spacePosRoisArch = 0
		
		Select cmdRoisArch$
			Case "!CRYPT!"
				RuntimeError("Encryption header must always be first!")
				
			Case "." ; Create files
				spacePosRoisArch = Instr(argsRoisArch$ + " ", " ")
				If spacePosRoisArch <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
				argRoisArch1$ = Left$(argsRoisArch$, spacePosRoisArch - 1)
				If Not argRoisArch1$ Then RuntimeError("Filename not given.")
				If argRoisArch1$ = fileName$ Then 
					argRoisArch2 = Int(Mid$(argsRoisArch$, spacePosRoisArch + 1))
					file = WriteFile(argRoisArch1$)
					If argRoisArch2 Then 
						For i=1 To argRoisArch2
							WriteLine(file, ReadLine$(roisArchHandle))
						Next
					Else
					EndIf
					CloseFile(file)
					Return
				EndIf
			
			Case "/." ; Create Base64 encoded files.
				spacePosRoisArch = Instr(argsRoisArch$ + " ", " ")
				If spacePosRoisArch <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
				argRoisArch1$ = Left$(argsRoisArch$, spacePosRoisArch - 1)
				If Not argRoisArch1$ Then RuntimeError("Filename not given.")
				If argRoisArch1$ = fileName$ Then 
					file = WriteFile(argRoisArch1$)
					entireFileData$ = ""
					finished = False
					Repeat
						Base64Line$ = ReadLine$(roisArchHandle)
						If Base64Line = "~" Then finished = True
						
						If finished = False Then 
							entireFileData$ = entireFileData$ + Base64Line$
						EndIf
					Until finished = True
					finished = False
					byteCount = Base64DecodeBytes(entireFileData$)
					For i=0 To byteCount - 1
						WriteByte(file, RoisArcBytesOut(i))
					Next
					CloseFile(file)
					Return
				EndIf
				
			
			; Not needed given this is only for reading files.
			Case "!"
				
			
			Case ";"
				
			
			Case ":"
				
						
			Case "?"
				
			
			Default
				RuntimeError("Not a valid v1.1 Rois Archive!")
				
		End Select
	Wend
End Function

Function BinToInt(val$)
	Local i, n, result
	For i = 1 To Len(val$)
		n = Mid$(val$, i, 1)
		result = result * 2 + n
	Next
	Return result And 255
End Function

Function Base64DecodeBytes(encodedInput$)
	
	Local i, c, padCount, buffer$, bitLen, byteCount
	
	; Strip padding and count it
	While Right$(encodedInput$, 1) = "="
		encodedInput$ = Left$(Input$, Len(encodedInput$) - 1)
		padCount = padCount + 1
	Wend
	
	; Convert to 6-bit binary string
	For i = 1 To Len(encodedInput$)
		c = Instr(base64Chars$, Mid$(encodedInput$, i, 1))
		If c = 0 Then Return False ; Invalid character
		c = c - 1
		buffer$ = buffer$ + Right$("000000" + Bin$(c), 6)
	Next
	
	bitLen = Len(buffer$)
	byteCount = (bitLen / 8) - padCount
	
	; Create output byte array
	Dim RoisArcBytesOut(byteCount - 1)
	
	For i = 0 To byteCount - 1
		RoisArcBytesOut(i) = BinToInt(Mid$(buffer$, i * 8 + 1, 8))
	Next
	
	Return byteCount
End Function