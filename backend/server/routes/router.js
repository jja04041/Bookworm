const express = require('express'); //express를 사용하기 위함.
const multer = require('multer'); //이미지 업/다운로드를 위함.
const fs = require('fs');
const fb = require("../module/firebaseProcess"); //파이어베이스 관련 함수 모음 
const router = express.Router();
module.exports = router;
const {createFirebaseToken} = require("../module/firebase/getToken");
var imgPath = "";
var Path = "";

//Path List
const LocalFeedImgPath = "./Image/feed/";
const LocalProfileImgPath = "./Image/profileimg/";
const LocalAlbumImgPath = "./Image/albumimg/";


// Main -> 어플 소개페이지로 사용할 듯 하다. 
router.get("/", (req, res) => {
  console.log(`${writeAccessLog(req.ip)} - 메인 페이지 접속`); 
  res.send("서버가 현재 작동중입니다.");
});


//for image 
var _storage = multer.diskStorage({
  destination: function (req, file, cb) {
    var arr = file.originalname.split('_');

    if (arr[0] == "feed") {
      Path = LocalFeedImgPath;
      //폴더가 없다면 생성한다. 
      if (!fs.existsSync(Path)) {
        fs.mkdirSync(Path);
      }

    } else if (arr[0] == "profile") {
      Path = LocalProfileImgPath;
      //폴더가 없다면 생성한다.
      if (!fs.existsSync(Path)) {
        fs.mkdirSync(Path);
      }
    } else if (arr[0] == "album") {
      Path = LocalAlbumImgPath;
      //폴더가 없다면 생성한다.
      if (!fs.existsSync(Path)) {
        fs.mkdirSync(Path);
      }
    }


    cb(null, Path);
  },
  filename: function (req, file, cb) {
    var arr = file.originalname.split('_');
    switch (Path) {
      case LocalProfileImgPath:
        imgfile = `${arr[1]}`;
        imgPath = "/getprofileimg/" + imgfile;
        break;
      case LocalFeedImgPath:
        imgfile = `${arr[1]}_${arr[2]}`;
        imgPath = "/getimage/" + imgfile;
        break;
      case LocalAlbumImgPath:
        imgfile = `${arr[1]}_${arr[2]}`;
        imgPath = "/getalbumimg/" + imgfile;
        break;
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
    if (imgPath.includes("/getimage/")) console.log(`${writeAccessLog(req.ip)} - ${imgPath}  피드 이미지가 업로드 되었습니다`);  
    else if(imgPath.includes("/getprofileimg/"))console.log(`${writeAccessLog(req.ip)} - ${imgPath}  프로필 이미지가 업로드 되었습니다`);
    else console.log(`${writeAccessLog(req.ip)} - ${imgPath}  앨범 이미지가 업로드 되었습니다`);
  } catch (err) {
    console.log(`${writeAccessLog(req.ip)} - `);
    console.dir(err.stack);
    res.status(400);
  }

});

/* 앨범 이미지 다운로드  */

//프로필 이미지
router.use('/getprofileimg/:data', (req, res) => {
  const dataPath = LocalProfileImgPath + req.params.data; //피드 이미지 경로 숨기기 
  fs.readFile(dataPath, function (err, data) {
    if (err) {
      console.log(`${writeAccessLog(req.ip)} - ${dataPath} 조회 실패`);
      return res.status(404).end();
    } // Fail if the file can't be read.
    console.log(`${writeAccessLog(req.ip)} - ${dataPath} 조회 완료`);
    res.writeHead(200, {
      'Content-Type': 'image/jpeg'
    });
    res.end(data); // Send the file data to the browser.
  });
});

//피드 이미지
router.use('/getimage/:data', (req, res) => {
  const dataPath = LocalFeedImgPath + req.params.data; //피드 이미지 경로 숨기기 
  //서버 접속기록 생성 

  fs.readFile(dataPath, function (err, data) {
    if (err) {
      console.log(`${writeAccessLog(req.ip)} - ${dataPath} 조회 실패`);
      return res.status(404).end();
    } // Fail if the file can't be read.
    console.log(`${writeAccessLog(req.ip)} - ${dataPath} 조회 완료`);
    res.writeHead(200, {
      'Content-Type': 'image/jpeg'
    });
    res.end(data); // Send the file data to the browser.
  });
});

//앨범 이미지
router.use('/getalbumimg/:data', (req, res) => {
  const dataPath = LocalAlbumImgPath + req.params.data; //피드 이미지 경로 숨기기 
  fs.readFile(dataPath, function (err, data) {
    if (err) {
      console.log(`${writeAccessLog(req.ip)} - ${dataPath} 조회 실패`);
      return res.status(404).end();
    } // Fail if the file can't be read.
    console.log(`${writeAccessLog(req.ip)} - ${dataPath} 조회 완료`);
    res.writeHead(200, {
      'Content-Type': 'image/jpeg'
    });
    res.end(data); // Send the file data to the browser.
  });
});



/* 이미지 삭제 */


//피드 삭제 시 이미지 삭제 
router.post("/deleteImg", (req, res) => {
  const fileName = req.body.feedId + ".jpg";
  const userToken = req.body.userToken;
  const filePath = LocalFeedImgPath + fileName;

  
  if (req.body.feedId.split('_')[1] == userToken) {
    //파일이 있는지 여부를 먼저 확인 
    fs.access(filePath, fs.constants.F_OK, (err) => {
      if (err) {
        console.log(`${fileName}은/는 삭제할 수 없는 파일입니다`);
        return res.sendStatus(504);
      }
      //파일 삭제 진행 
      fs.unlink(filePath, (err) => {
        if (err) {
          console.log(err);
          return res.sendStatus(504);
        }
        console.log(`${fileName} 을/를 정상적으로 삭제했습니다`);
        return res.sendStatus(200);
      })
    })
  } else return res.sendStatus(401); //유저 토큰과 일치 하지 않은 경우 401에러 표시 
})
//토큰 관리 
// router.post("/token", (req, res) => {
//   const token = req.body.token;
//   const platform = req.body.platform;
//   if (!token) return res.status(400).send({
//       error: 'There is no token.'
//     })
//     .send({
//       message: 'Access token is a required parameter.'
//     });

//   console.log(`Verifying Kakao token: ${token}`);
//   //토큰 생성 
//   if (platform == "kakao") {
//     createFirebaseToken(token, platform).then((firebaseToken) => {
//       console.log(`Returning firebase token to user: ${firebaseToken}`);
//       console.log(firebaseToken);
//     });
//   }
// });

// router.get("/token", (req, res) => {
//   const token = req.query.token;
//   const platform = req.query.platform;
//   if (!token) return res.status(400).send({
//       error: 'There is no token.'
//     })
//     .send({
//       message: 'Access token is a required parameter.'
//     });

//   console.log(`Verifying Kakao token: ${token}`);
//   //토큰 생성 
//   if (platform == "kakao") {
//     createFirebaseToken(token, platform).then((firebaseToken) => {
//       console.log(`Returning firebase token to user: ${firebaseToken}`);
//       console.log(firebaseToken);
//     });
//   }
// });



//사용자 탈퇴시 실행하는 쿼리 
router.post("/removeuser", async (req, res) => {
  var token = req.query.token;
  fb.showlist(token).then((answer) => {
    if (answer) res.send("done");
  });
});

//서버 접속시 로그 남김 
var writeAccessLog=((remoteAddress)=>{
  const now = new Date();
  now.setHours(now.getHours()+9);
  var date=["일","월","화","수","목","금","토"];
  var stringDate=`${now.getFullYear()}년${now.getMonth()+1}월${now.getDate()}일( ${date[now.getDay()]}) ${now.getHours()}시${now.getMinutes()}분`   
  const newLog = `${stringDate}: IP(${remoteAddress})`;
  return newLog;
});