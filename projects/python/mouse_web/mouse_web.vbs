Dim WinScriptHost
Set WinScriptHost = CreateObject("WScript.Shell")
WinScriptHost.Run Chr(34) & "C:\Users\InsoGamer\Desktop\mouse_web\mouse_web.bat" & Chr(34), 0
Set WinScriptHost = Nothing