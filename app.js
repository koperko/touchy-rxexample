var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

server.listen(3001);

app.get('/', function (req, res) {
  res.sendfile(__dirname + '/index.html');
});


var id = 1;

io.on('connection', function (socket) {

  console.log("connected new user " + id++ )
  console.log(socket.handshake.headers["user-agent"]);
  //odesílá id usera
  socket.emit('news', { user: id });

  socket.emit('bxy', { hello: 'bxy' });
  socket.on('my other event', function (data) {
    console.log(data);
  });



  socket.on("xy",function(data){
        console.log(data);
        socket.broadcast.emit("bxy",data);

    });


});
