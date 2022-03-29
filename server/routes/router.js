const express = require('express'); //express를 사용하기 위함.
const multer = require('multer'); //이미지 업/다운로드를 위함.
const fs = require('fs');
const router = express.Router();
module.exports = router;
var imgPath = "";
var Path = "";
require('dotenv').config({
  path: path.join(__dirname, '.env')
});
const {
  OAuth2Client
} = require('google-auth-library');
const requestMeUrl = 'https://kapi.kakao.com/v2/user/me';
const request = require('request-promise');
module.exports = router;
const {
  initializeApp,
  cert
} = require('firebase-admin/app');
const firebaseAdmin = require('firebase-admin');

const serviceAccount = require(path.join(__dirname, 'bookworm-f6973-firebase-adminsdk-lzs4b-a45e20e976.json'));
initializeApp({
  credential: cert(serviceAccount)
});


//for image 
var _storage = multer.diskStorage({
  destination: function (req, file, cb) {
    var arr = file.originalname.split('_');

    if (arr[0] == "feed") {
      Path = "./feed";
      if (!fs.existsSync(Path)) {
        fs.mkdirSync(Path);
      }

    } else {
      Path = "./profileimg";
      if (!fs.existsSync(Path)) {
        fs.mkdirSync(Path);
      }
    }

    cb(null, Path);
  },
  filename: function (req, file, cb) {
    var arr = file.originalname.split('_');
    imgfile = `${arr[1]}_${arr[2]}`;
    if (Path == "./profileimg") {
      imgPath = "/getprofileimg/" + imgfile;
    } else if (Path == "./feed") {
      imgPath = "/getimage/" + imgfile;
    }

    cb(null, imgfile); //콜백을 통해 파일명을 설정
  }
});
const upload = multer({
  storage: _storage
}); // 미들웨어 생성

//이미지 업로드 수신 => 서버에 저장 후, 이미지 경로 리턴 
router.post('/upload', upload.single('upload'), (req, res) => {
  try {
    res.status(200).send(imgPath);

  } catch (err) {

    console.dir(err.stack);
  }

});


//이미지를 다운로드 받을 때(피드 이미지 )
router.use('/getimage/:data', (req, res) => {

  const dataPath = "./feed/" + req.params.data;
  fs.readFile(dataPath, function (err, data) {
    if (err) {
      return res.status(404).end();
    } // Fail if the file can't be read.
    res.writeHead(200, {
      'Content-Type': 'image/jpeg'
    });
    res.end(data); // Send the file data to the browser.
  });
});

router.get("/", (req, res) => {
  res.send("hello,World!");
});
