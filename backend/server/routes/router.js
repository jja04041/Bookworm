const express = require('express'); //express를 사용하기 위함.
const multer = require('multer'); //이미지 업/다운로드를 위함.
const fs = require('fs');
const fb= require("../module/firebaseProcess"); //파이어베이스 관련 함수 모음 
const e = require('express');
const router = express.Router();
module.exports = router;
var imgPath = "";
var Path = "";

//Path List
const LocalFeedImgPath="./Image/feed/"
const LocalProfileImgPath="./Image/profileimg/"


//Main
router.get("/", (req, res) => {
  res.send("helloworld~");
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
      //해당 폴더에 사용자 프로필 사진이 남아있는 경우 다 지운다. 


    }

    cb(null, Path);
  },
  filename: function (req, file, cb) {
    var arr = file.originalname.split('_');
    imgfile = `${arr[1]}_${arr[2]}`;
    if (Path == LocalProfileImgPath) {
      imgPath = "/getprofileimg/" + imgfile;
    } else if (Path ==LocalFeedImgPath) {
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
    if(imgPath.includes("/getimage/")) console.log(imgPath +" 피드 이미지가 업로드 되었습니다");
    else console.log(imgPath +" 프로필 이미지가 업로드 되었습니다");
  } catch (err) {

    console.dir(err.stack);
  }

});


//이미지를 다운로드 받을 때(프로필 이미지 )
router.use('/getprofileimg/:data', (req, res) => {
  const dataPath =LocalProfileImgPath + req.params.data; //피드 이미지 경로 숨기기 
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

//이미지를 다운로드 받을 때(피드 이미지 )
router.use('/getimage/:data', (req, res) => {
  const dataPath = LocalFeedImgPath + req.params.data; //피드 이미지 경로 숨기기 
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





//피드 삭제 시 이미지 삭제 
router.post("/deleteImg", (req, res) => {
  const fileName = req.body.feedId+".jpg";
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
  }else return res.sendStatus(401); //유저 토큰과 일치 하지 않은 경우 401에러 표시 
})
//토큰 관리 
router.get("/token", (req, res) => {
  const token = req.query.token;
  const platform = req.query.platform;
  if (!token) return res.status(400).send({
      error: 'There is no token.'
    })
    .send({
      message: 'Access token is a required parameter.'
    });

  console.log(`Verifying Kakao token: ${token}`);
  //토큰 생성 
  if (platform == "kakao") {
    createFirebaseToken(token, platform).then((firebaseToken) => {
      console.log(`Returning firebase token to user: ${firebaseToken}`);
      console.log(firebaseToken);
    });
  }
});


//사용자 탈퇴시 실행하는 쿼리 


router.post("/showlist",async (req,res)=>{
  var token = req.query.token;
  //팔로워 삭제 
  fb.showlist(token).then((answer)=> {
      if(answer) res.send("done");
  });
  //피드 삭제

  //댓글 삭제 

  //회원 정보 삭제 
});
