# touchy-rxexample
RxAndroid training 

Hey there! ... My colleague and I decided one evening, that we want to try the socket.io library and I saw it as a perfect opportunity for myself to try some Reactive-Functional programming.

As I was saying, it was an evening effort, so there is practically no UI around this. Only a drawing canvas, which continuously erases the drawn line after 5 seconds and replicates the line on another device in realtime as you draw it. Right now, every device connected to one server is practically sharing one canvas with other. 

Although There can be multiple clients, android app is not ready now for 2+ receiving clients. It stores the received points into one queue and so, the resulting line is somewhat a combination of all received information. If you would like to continue, server is already sending information about the userId, from whom the line is coming. 

There is also a pretty simple observable for emitting events, as they come through the socket.io library. 

But one thing is clear from that night. I regret, I didn't came across reactive programming sooner! I know that my execution was not the purest ( if at all ) reactive approach, but I really love the concept and will be exploring it further! Any suggestions are welcomed..

Credits for node.js server and html canvas for displaying the lines on the web goes to Radim Stepanik! great work .. 

PS: If you are wondering, why the project is named RxKotlinExample. Previously, I was going to write it in kotlin, as I really liked the syntax when I was exploring Rx principles on the web and thought I would give it a try. But trying two new approaches at the same time was not a great idea and after few moments, I decided to write it in good old Java. Maybe I'll refactor it, if I'll get to it someday :))

