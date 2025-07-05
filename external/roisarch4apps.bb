; Rois Archive External Parser V1.1
; Usage: Include in your Blitz3D Applications to utilize Rois 
; Archives, eg. Portable executables, non directly shown files 
; (great for secrets btw) correction: NOT GREAT AT ALL.
; Encryption soon :D

; NOTE: Encryption is an external parser feature exclusive currently.

; I used to use globals for easy debugging, but I think I can switch to
; local variables for the functions now.

; This function creates the file you require within the current directory.
; Also, make sure to use CloseFile() before finalizing/ending.
; Pass key as "" if archive isn't encrypted.
Function extractFromRoisArcFile(roisArcFile$, fileName$, key$)
	Local roisArchHandle = ReadFile(roisArcFile$)
	If Not roisArchHandle Then RuntimeError("Archive file does not exist or is inaccessible.")
	Local argRoisArch1$ = ""
	Local argRoisArch2 = 0
	Local cmdRoisArch$ = ""
	Local argsRoisArch$ = ""
	Local spacePosRoisArch = 0
	Local archKey = ""
	Line$ = Trim(ReadLine$(roisArchHandle))
	
	; Skip empty lines using label. (Remnant from Rois Archive opener. Labels cannot exist in Functions)
	If Line$ = "" Then RuntimeError("External Parser is unable to parse some blank lines.")
	; Parse command and arguments
	spacePosRoisArch = Instr(Line$ + " ", " ")
	If spacePosRoisArch <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
	cmdRoisArch$ = Upper$(Left$(Line$, spacePosRoisArch - 1))
	argsRoisArch$ = Mid$(Line$, spacePosRoisArch + 1)
	
	spacePosRoisArch = 0
	If cmdRoisArch$ = "!CRYPT!" Then 
		If key$ = "" Then RuntimeError("This archive is encrypted. You must give a key.")
		spacePosRoisArch = Instr(argsRoisArch$ + " ", " ")
		If spacePosRoisArch <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
		
		; Check for correct key with pseudoMD5 and normal MD5.
		argRoisArch1$ = Left$(argsRoisArch$, spacePosRoisArch - 1)
		Local pseudoMD5XORResult$ = XORenc$(argRoisArch1$, PseudoMD5Hash(key$) + key$)
		If pseudoMD5XORResult$ = "correct" Then archKey = PseudoMD5Hash(key$) + key$
		
		; Check to do normal MD5
		If archKey = "" Then
			Local normalMD5XORResult$ = XORenc$(argRoisArch1$, GetMD5Hash(key$) + key$)
			If normalMD5XORResult$ = "correct" Then archKey = GetMD5Hash(key$) + key$
		EndIf
	EndIf
	While Not Eof(roisArchHandle)
		argRoisArch1$ = ""
		argRoisArch2 = 0
		cmdRoisArch$ = ""
		argsRoisArch$ = ""
		spacePosRoisArch = 0
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
							If Not archKey Then 
								WriteLine(file, ReadLine$(roisArchHandle))
							Else
								WriteLine(file, XORenc$(ReadLine$(roisArchHandle), archKey))
							EndIf
						Next
					Else
					EndIf
					CloseFile(file)
					Return
				EndIf
			
			Case "!"
				
			
			Case ";"
				
			
			Case ":"
				
						
			Case "?"
				
			
			Default
				RuntimeError("Not a valid v1.1 Rois Archive!")
				
		End Select
	Wend
End Function

; The following functions are taken from obfusc_functions.bb


; Stops cheaters from snooping in files, but not great to determined hackers.
Function XORenc$(passedTextorVal$, key$)
	Local result$ = ""
	For i = 1 To Len(passedTextorVal$)
		result$ = result$ + Chr(Asc(Mid$(passedTextorVal$, i, 1)) ~ Asc(Mid$(key$, ((i - 1) Mod Len(key$)) + 1, 1)))
	Next
	Return result$
End Function
; XP failsafe.
; This isn't secure though, please upgrade if you are still using XP.
Function PseudoMD5Hash$(toBeHashedInput$)
	Local a = $67452301
	Local b = $EFCDAB89
	Local c = $98BADCFE
	Local d = $10325476
	
	For i = 1 To Len(toBeHashedInput$)
		ch = Asc(Mid$(toBeHashedInput$, i, 1))
		a = (a + ((b And c) Or ((Not b) And d)) + ch) Mod $100000000
		b = (b + ((c And d) Or ((Not c) And a)) + ch * i) Mod $100000000
		c = (c + ((d And a) Or ((Not d) And b)) + ch * ch) Mod $100000000
		d = (d + ((a And b) Or ((Not a) And c)) + ch + i) Mod $100000000
	Next
	
	Return "NoMD5-" + Right$("00000000" + Hex(a), 8) + Right$("00000000" + Hex(b), 8) + Right$("00000000" + Hex(c), 8) + Right$("00000000" + Hex(d), 8)
End Function

; Used for getting a MD5 Hash for key lengthening. 
; Fails silently and uses PseudoMD5 if user is on Windows XP.
; Best for making XORenc, although simple, more secure.

; Modified a little to match External Parser needs.
Function GetMD5Hash$(toBeHashedInput$)
	hashInputFile = WriteFile("hashme.txt")
	WriteLine(hashInputFile, toBeHashedInput$)
	CloseFile(hashInputFile)
	ExecFile("cmd /c certutil -hashfile hashme.txt MD5 > hash.txt")
	Delay 150 ; Delay since older machines may take a short period of time unlike our current PCs.
	DeleteFile("hashme.txt")
	hashOutputFile = ReadFile("hash.txt")
	If Not hashOutputFile Then
		RuntimeError("User is using Windows XP or lower.")
	EndIf
	hash$ = ReadLine$(hashOutputFile)
	If Not hash$ Then
		RuntimeError("User is using Windows XP or lower.")
	EndIf
	Return hash$
End Function