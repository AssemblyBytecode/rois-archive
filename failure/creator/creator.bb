; creator.bb
; Rois Archive Creator
; Generates .roisarchive from a folder

AppTitle "Rois Archive Creator"
Graphics 640, 480, 0, 2

Global archiveFile = WriteFile("output.roisarchive")

WriteLine(archiveFile, "! Created with Rois Archive Creator")

folder$ = CommandLine$()
If folder$ = "" Then
	Print "Enter folder path:"
	folder$ = Input$()
EndIf

If FileType(folder$) <> 2 Then
	CloseFile(archiveFile)
	RuntimeError("Invalid Folder, please format as following: DriveLetter:\Path\To\Your\Folder\")
EndIf

baseLen = Len(folder$)
If Right$(folder$, 1) <> "\" And Right$(folder$, 1) <> "/" Then
	folder$ = folder$ + "\"
	baseLen = baseLen + 1
EndIf

Dim byteArray(1) ; Placeholder, will be overwritten.
ScanFolder(folder$)

CloseFile(archiveFile)
Print "Archive complete."
WaitKey()

; Recursive folder scanner
Function ScanFolder(path$)
	file = ReadDir(path$)
	loop = True
	While loop = True
		skip = False
		entry$ = NextFile$(file)
		If entry$ = "" Then 
			Print "Finished"
			Exit
		EndIf
		If entry$ = "." Or entry$ = ".." Then 
			skip = True 
		EndIf
		
		; Can't skip so we just use conditionals
		If skip = False Then
			full$ = path$ + entry$
			rel$ = GetFileFilename$(full$)
			
			If FileType(full$) = 2 Then
				; Directory
				Print "Adding Dir:"
				Print full$
				If Instr(rel$, "\") = 0 Then
					WriteLine(archiveFile, ": " + rel$)
				Else
					parent$ = StripDir$(rel$)
					name$ = StripPath$(rel$)
					WriteLine(archiveFile, "; " + parent$ + "\" + name$)
				EndIf
				ScanFolder(full$ + "\")
			ElseIf FileType(full$) = 1 Then
				; Treat as binary given we can't really count lines 
				; due to limited functionallity.
				Print "Adding file:"
				Print full$
				f = ReadFile(full$)
				b$ = ""
				CloseFile(f)
				; Sets byteArray and gets the array size to encode.
				arraySize = LoadBinaryFileToByteArray(full$)
				encoded$ = Base64EncodeFromBytes$(arraySize)
				lineCount = CountLines(encoded$)
				; /. tells that the file is in Base64 and needs to be decoded.
				; Also doesn't need line count :) but requires ~ to show
				; that's the end of the file.
				
				; Note: I may decide one day that . won't get updates
				; and will just be a
				WriteLine(archiveFile, "/. " + rel$)
				
				; Write 76-char lines
				For i = 1 To Len(encoded$) Step 76
					WriteLine(archiveFile, Mid$(encoded$, i, 76))
				Next
				WriteLine(archiveFile, "~")
			EndIf
		EndIf
	Wend
	CloseDir(file)
End Function

Function StripPath$(path$)
	For i = Len(path$) To 1 Step -1
		If Mid$(path$, i, 1) = "\" Then Return Mid$(path$, i + 1)
	Next
	Return path$
End Function

Function StripDir$(path$)
	For i = Len(path$) To 1 Step -1
		If Mid$(path$, i, 1) = "\" Then Return Left$(path$, i - 1)
	Next
	Return ""
End Function

Function CountLines(inputText$)
	lines = Len(Text$) / 76
	If Len(inputText$) Mod 76 > 0 Then lines = lines + 1
	Return lines
End Function

; This may never be introduced given Base64 is way better in terms of storage then plain text.
;Function IsTextSafe(file$)
;	f = ReadFile(file$)
;	While Not Eof(f)
;		b = ReadByte(f)
;		If b < 9 Or (b > 9 And b < 32) Or b > 126
;			CloseFile(f)
;			Return False
;		EndIf
;	Wend
;	CloseFile(f)
;	Return True
;End Function

Function GetFileFilename$(filePath$)
	; Find position of last backslash
	lastSlash = 0
	For i = Len(filePath$) To 1 Step -1
		If Mid$(filePath$, i, 1) = "\" Then
			lastSlash = i
			Exit
		EndIf
	Next
	
	fileName$ = Mid$(filePath$, lastSlash + 1)
	Return fileName$
End Function

Function LoadBinaryFileToByteArray(file$)
	f = ReadFile(file$)
	size = FileSize(file$)
	Dim byteArray(size)
	For i = 1 To size
		byteArray(i) = ReadByte(f)
	Next
	CloseFile(f)
	Return size
End Function

Function Base64EncodeFromBytes$(byteArraySize)
	Local base64chars$ = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
	encoded$ = ""
	i = 1
	length = (byteArraySize / 4)

	While i < length
		b1 = byteArray(i)
		b2 = 0 : b3 = 0
		If i + 1 <= length Then b2 = byteArray(i + 1)
		If i + 2 <= length Then b3 = byteArray(i + 2)

		c1 = b1 Shr 2
		c2 = (b1 And 3) Shl 4 Or (b2 Shr 4)
		c3 = (b2 And 15) Shl 2 Or (b3 Shr 6)
		c4 = b3 And 63

		encoded$ = encoded$ + Mid$(base64chars$, c1 + 1, 1)
		encoded$ = encoded$ + Mid$(base64chars$, c2 + 1, 1)

		If i + 1 <= length
			encoded$ = encoded$ + Mid$(base64chars$, c3 + 1, 1)
		Else
			encoded$ = encoded$ + "="
		EndIf

		If i + 2 <= length
			encoded$ = encoded$ + Mid$(base64chars$, c4 + 1, 1)
		Else
			encoded$ = encoded$ + "="
		EndIf

		i = i + 3
	Wend
	Return encoded$
End Function