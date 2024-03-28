# StaffNotes
###Minecraft server plugin to create and save player specific notes

Simple Minecraft plugin that adds the command /notes so that server admins can store and review notes on players. 

Can be used to store information such previous staff interactions both good or bad or as a quick Ban Evasion reference for players whos sibilines or roommates get banned but they still want to play. 

Notes are stored based on user UUID so will persist username changes. 
Notes can be made for players who have never joined the server

Commands:
/notes Add <PlayerName> <NoteType> <Note>
/notes Remove <PlayerName> [NoteType]
/notes Get <PlayerName> [NoteType]
/notes Get All

Permissions:
notes.default    Plugin won't listen to you without this permission
notes.add        Gives permissions to add notes
notes.remove     Gives permissions to remove notes
notes.view       Gives permissions to get notes
notes.view.all   Gives permissions to get all notes
