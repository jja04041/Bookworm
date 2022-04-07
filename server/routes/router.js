const express = require('express'); //express를 사용하기 위함.
const multer = require('multer'); //이미지 업/다운로드를 위함.
const fs = require('fs');
const router = express.Router();
// const {
//   createFirebaseToken
// } = require("../module/firebase/getToken")
module.exports = router;
var imgPath = "";
var Path = "";
const {
  initializeApp,
  cert
} = require('firebase-admin/app');
const serviceAccount = require('./bookworm-f6973-firebase-adminsdk-lzs4b-a45e20e976.json');
const {
  getFirestore,
  Timestamp,
  FieldValue
} = require('firebase-admin/firestore');
initializeApp({
  credential: cert(serviceAccount)
});
const db = getFirestore();


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

router.get("/",(req,res)=>{
  res.send("helloworld~");
});
router.get("/:token", async (req, res) => {
  var value = req.params.token;
  checkID(value).then((token) => {
    res.send(token);
  });
});

async function checkID(token) {
  var ID = token;
  var find=false;
  const snapshot = await db.collection('users').get();
  snapshot.forEach((doc) => {
    console.log(doc.id,ID,doc.id==ID);
    if (doc.id==ID) find=true;
  });
  return find;
};

// router.get("/token", async(req, res) => {
//   const {OAuth2Client} = require('google-auth-library');
//   const CLIENT_ID=process.env.CLIENT_ID;
//   const token=req.query.token;
// const client = new OAuth2Client(CLIENT_ID);
// async function verify() {
//   const ticket = await client.verifyIdToken({
//       idToken: token,
//       audience: CLIENT_ID,  // Specify the CLIENT_ID of the app that accesses the backend
//       // Or, if multiple clients access the backend:
//       //[CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3]
//   });
//   const payload = ticket.getPayload();
//   const userid = payload['sub'];
//   // If request specified a G Suite domain:
//   // const domain = payload['hd'];
// }
// verify().catch(console.error);
// const url=`https://oauth2.googleapis.com/tokeninfo?id_token=${token}`;
// request.get(url,(req,res)=>{
//   console.log(req);
//   console.log(res.body);
// })
// });