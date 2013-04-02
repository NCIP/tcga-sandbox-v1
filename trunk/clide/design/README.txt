The design files in this directory are written in the format required for this tool, the Quick Sequence Diagram Editor:
http://sdedit.sourceforge.net/index.html

There is no visual editor, just a simple (but expressive) markup language.  For a complete list of command line options
run the command:  java -jar sdedit-3.1.jar -h

examples runs:

%java -jar sdedit-3.1.jar -o attempt_to_schedule.png -t png attempt_to_schedule.sd
%java -jar sdedit-3.1.jar -o client_alerts_server.png -t png client_alerts_server.sd
%java -jar sdedit-3.1.jar -o server_requests_files.png -t png server_requests_files.sd

