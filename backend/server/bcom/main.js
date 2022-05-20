const express = require('express'); //서버를 여는 모듈
// const nunjucks = require("nunjucks");
var app = express();
const port = 3000;
const hostname = "10.0.0.14" //내부 IP 주소
var mysql = require('mysql');
var connection = mysql.createConnection({
    host: '132.226.234.108:3306', // 호스트 주소
    user: 'bookworm2022', // mysql user
    password: 'bookworm2022ft!', // mysql password
    database: 'bookSearch' // mysql 데이터베이스
});


app.use('/index.html', express.static(__dirname + '/views'));

app.get('/', (req, res) => {
    connection.connect();
    connection.query('SELECT * from ORDERS',
        function (error, results, fields) {
            if (error) throw error;
            console.log('The solution is: ', results[0]);
        });
    connection.end();
    res.redirect("/index.html");

});
app.get('/index.html', (req, res) => {
    res.sendFile(__dirname + "/views/index.html");
})
app.get('/index.html/', (req, res) => {
    var name = req.query.keyword;
    console.log(name);
})

//서버 개방
app.listen(port, hostname, () => {
    console.log(`Server running at https://${hostname}:${port}/`);
})
