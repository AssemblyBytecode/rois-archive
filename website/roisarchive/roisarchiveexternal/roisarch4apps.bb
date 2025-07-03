; Rois Archive External Parser V1.0
; Usage: Include in your Blitz3D Applications to utilize Rois 
; Archives, eg. Portable executables, non directly shown files 
; (great for secrets btw).
; Encryption soon :D

Global roisArcOutputFileHandle = 0
Global roisArcFileHandle = 0
Global argRoisArch1$ = ""
Global argRoisArch2 = 0
Global cmdRoisArch$ = ""
Global argsRoisArch$ = ""
Global spacePosRoisArch = 0

; This function creates the file you require within the current directory.
; Also, make sure to use CloseFile() before finalizing/ending.
Function extractFromRoisArcFile(roisArcFile$, fileName$)
	roisArchHandle = ReadFile(roisArcFile$)
	While Not Eof(roisArchHandle)
		argRoisArch1$ = ""
		argRoisArch2 = 0
		cmdRoisArch$ = ""
		argsRoisArch$ = ""
		spacePosRoisArch = 0
		Line$ = Trim(ReadLine$(roisArchHandle))
		
		; Skip empty lines using label
		If Line$ = "" Then RuntimeError("External Parser is unable to parse blank lines.")
		; Parse command and arguments
		spacePosRoisArch = Instr(Line$ + " ", " ")
		If spacePosRoisArch <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
		cmdRoisArch$ = Upper$(Left$(Line$, spacePosRoisArch - 1))
		argsRoisArch$ = Mid$(Line$, spacePosRoisArch + 1)
		
		spacePosRoisArch = 0
		
		Select cmdRoisArch$
				
			Case "." ; Create files
				spacePosRoisArch = Instr(argsRoisArch$ + " ", " ")
				If spacePosRoisArch <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
				argRoisArch1$ = Left$(argsRoisArch$, spacePosRoisArch - 1)
				If Not argRoisArch1$ Then RuntimeError("Filename not given.")
				If argRoisArch1$ = fileName$ Then 
					argRoisArch2= Int(Mid$(argsRoisArch$, spacePosRoisArch + 1))
					file = WriteFile(argRoisArch1$)
					If argRoisArch2 Then 
						For i=1 To argRoisArch2
							WriteLine(file, ReadLine(roisArchHandle))
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
					RuntimeError("Not a valid v1.0 Rois Archive!")
				
		End Select
	Wend
End Function