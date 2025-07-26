; roisarch.bb
; Uses some code from roisarch4apps.bb

ShowPointer()
AppTitle "Rois Archive Opener"

archivePath$ = CommandLine$()

If archivePath$ = "" Then
	Print "Enter archive path:"
	archivePath$ = Input$()
EndIf

If FileType(archivePath$) <> 1 Then RuntimeError("Path To archive file is invalid. Format as so: DriveLetter:\Path\To\Archive\File.roisarchive")

; Find the last backslash manually
lastSlash = 0
For i = 1 To Len(archivePath$)
	If Mid$(archivePath$, i, 1) = "\"
		lastSlash = i
	EndIf
Next

date$ = CurrentDate()
date$ = Replace(date$, " ", "_")

time$ = CurrentTime()
time$ = Replace(time$, ":", "-")

archiveFolderName$ = "RoisArchive_" + date$ + "_" + time$

CreateDir CurrentDir$() + archiveFolderName$

arcFile = ReadFile(archivePath$)
If Not arcFile Then RuntimeError("No File!")

ChangeDir archiveFolderName$

Global arg1$ = ""
Global arg2$ = ""
Global cmd$ = ""
Global args$ = ""
Global spacePos

Dim RoisArcBytesOut(1)

While Not Eof(arcFile)
	; Stop previous arguments and commands from "coming back from the dead"
	arg1$ = ""
	arg2$ = ""
	cmd$ = ""
	args$ = ""
	spacePos = 0
	Line$ = Trim(ReadLine$(arcFile))
	
	; Skip empty lines using label
	If Line$ = "" Then Goto SkipLine
	
	; Parse command and arguments
	spacePos = Instr(Line$ + " ", " ")
	If spacePos <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
	cmd$ = Upper$(Left$(Line$, spacePos - 1))
	args$ = Mid$(Line$, spacePos + 1)
	
	spacePos = 0
	
	Select cmd$
		
		Case "." ; Create files
			spacePos = Instr(args$ + " ", " ")
			If spacePos <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
			arg1$ = Left$(args$, spacePos - 1)
			If Not arg1$ Then RuntimeError("File doesn't have a filename.")
			arg2 = Int(Mid$(args$, spacePos + 1))
			file = WriteFile(arg1$)
			Print "Creating File: " + arg1$
			Print "Writing " + arg2 + " lines."
			If arg2 Then 
				For i=1 To arg2
					WriteLine(file, ReadLine(arcFile))
				Next
			Else
				Print "This file has no data."
			EndIf
			CloseFile(file)
		
		Case "/."
			spacePos = Instr(args$ + " ", " ")
			If spacePos <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
			arg1$ = Left$(args$, spacePos - 1)
			If Not arg1$ Then RuntimeError("File doesn't have a filename.")
			Print "Creating File:" + arg1$
			file = WriteFile(arg1$)
			entireFileData$ = ""
			finished = False
			Print "Reading Base64"
			Repeat
				Base64Line$ = ReadLine$(arcFile)
				If Base64Line = "~" Then finished = True
				Print Base64Line
				;If archKey Then Base64Line$ = XORenc(Base64Line$, key)
				
				If finished = False Then 
					entireFileData$ = entireFileData$ + Base64Line$
				EndIf
			Until finished = True
			finished = False
			byteCount = Base64DecodeBytes(entireFileData$)
			Print "Writing Bytes: " + byteCount
			For i=1 To byteCount
				Print "Writing Byte: " + RoisArcBytesOut(i)
				WriteByte(file, RoisArcBytesOut(i))
			Next
			CloseFile(file)
		
		Case ";" ; Create Subdirectory
			spacePos = Instr(args$ + " ", " ")
			If spacePos <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
			arg1$ = Left$(args$, spacePos - 1)
			arg2$ = Mid$(args$, spacePos + 1)
			
			If arg1$ = "" Then RuntimeError("No directory given.")
			If arg2$ = "" Then RuntimeError("Subdirectory doesn't have a name.")
			
			Print "Creating Subdirectory: " + arg2$
			Print arg1$ + "\" + arg2$
			CreateDir arg1$ + "\" + arg2$
		
		Case ":" ; Create directory
			spacePos = Instr(args$ + " ", " ")
			If spacePos <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
			arg1$ = Left$(args$, spacePos - 1)
			Print  arg1$
			If Not arg1$ Then RuntimeError("Directory doesn't have a name.")
			
			CreateDir arg1$
				
				
		Case "?" ; Execute CMDs or files (Removed)
			Print "Unable to run."
		
		Case "!" ; Add Comments during opening process.
			Print "[--------------------]"
			Print "Author Archive Comment"
			Print args$
			Print "[____________________]"
			Delay 1500
		
		Default
			RuntimeError "Not a valid v1.1 Rois Archive!"
		
	End Select

.SkipLine
Wend

ExecFile "explorer.exe " + CurrentDir$()
CloseFile arcFile
Print "Completed Extraction"
WaitKey()

Function BinToInt(val$)
	Local i, n, result
	For i = 1 To Len(val$)
		n = Mid$(val$, i, 1)
		result = result * 2 + n
	Next
	Return result And 255
End Function

Function Base64DecodeBytes(encodedInput$)
	Print encodedInput$
	Local i, c, padCount, buffer$, bitLen, byteCount
	
	; Strip padding and count it
	While Right$(encodedInput$, 1) = "="
		encodedInput$ = Left$(encodedInput$, Len(encodedInput$) - 1)
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

	Print "Returning ByteCount: "
	Print byteCount
	Delay 1500
	Return byteCount
End Function

End