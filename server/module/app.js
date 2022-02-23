
const express = require('express'); //서버를 여는 모듈
const router=require('../routes/router'); //route 모듈을 가져옴 => 라우터를 위함.
var app = express();
const port = 3000;
const hostname = "10.0.0.14" //내부 IP 주소

// //사용자의 post body를 처리하기 위한 미들웨어
app.use(express.json());
app.use(express.urlencoded({
    extended: true
}))
// //user 토큰이 있어야 서버 이용이 가능=> 어플 내에서만 웹 접근이 가능하도록 하기 위함.
// app.use((req,res,next)=>{ 
//     var a=req.header('user-token');
//     if (a==undefined||a!='dbproject2021'){
//         res.status(403);
//         res.send('forbidden error');
//     }else{
//         res.status(200);
//         next();
//     }

// })

app.use('/',router); //라우터 사용
//서버 개방
app.listen(port, hostname, () => {
    console.log(`Server running at https://${hostname}:${port}/`);
})


