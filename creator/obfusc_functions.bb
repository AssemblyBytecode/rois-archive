; obfusc_functions.bb
; Provides functions to encrypt Rois Archives LIGHTLY 
; (meaning useless against those determined, but good enough To keep 
; secrets from the average game player/cheater).
; As of version 1.1, this is only usable in External Parser.

; May require Windows Vista or higher.

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
Function GetMD5Hash$(toBeHashedInput$)
	hashInputFile = WriteFile("hashme.txt")
	WriteLine(hashInputFile, toBeHashedInput$)
	CloseFile(hashInputFile)
	ExecFile("cmd /c certutil -hashfile hashme.txt MD5 > hash.txt")
	Delay 150 ; Delay since older machines may take a short period of time unlike our current PCs.
	DeleteFile("hashme.txt")
	hashOutputFile = ReadFile("hash.txt")
	If Not hashOutputFile Then
		Print "File doesn't exist."
		Delay 1500
		Print "Returning PseudoMD5Hash."
		Delay 750
		Return PseudoMD5Hash$(toBeHashedInput$)
	EndIf
	hash$ = ReadLine$(hashOutputFile)
	If Not hash$ Then
		Print "No hash was given."
		Delay 1500
		Print "Returning PseudoMD5Hash."
		Delay 750
		Return PseudoMD5Hash$(toBeHashedInput$)
	EndIf
	Return hash$
End Function