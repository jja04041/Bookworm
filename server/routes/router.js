var express = require('express'); //express를 사용하기 위함.
const path = require('path');
const multer = require('multer'); //이미지 업/다운로드를 위함.
var router = express.Router();
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
  destination: 'uploads/',
  filename: function (req, file, cb) {
    return crypto.pseudoRandomBytes(16, function (err, raw) {
      if (err) {
        return cb(err);
      }
      return cb(null, file.originalname);
    });
  }
});


router.post('/upload', (req, res) => {
  multer({
      storage: _storage
    }).single('upload'),
    function (req, res) {
      try {
        let file = req.file;
        let originalName = '';
        let fileName = '';
        let mimeType = '';
        let size = 0;

        if (file) {
          originalName = file.originalname;
          filename = file.fileName; //file.fileName
          mimeType = file.mimetype;
          size = file.size;
          console.log("execute" + fileName);
        } else {
          console.log("request is null");
        }

      } catch (err) {

        console.dir(err.stack);
      }
      console.log(req.file);
      console.log(req.body);
      res.redirect("/uploads/" + req.file.originalname); //fileName
      return res.status
//for firebase
//커스텀 토큰 생성 

function createFirebaseToken(kakaoAccessToken) {
  return requestMe(kakaoAccessToken).then((response) => {
    const body = JSON.parse(response);
    console.log(body);
    const userId = `kakao:${body.id}`;
    if (!userId) {
      return res.status(404)
        .send({
          message: 'There was no user with the given access token.'
        });
    }
    let nickname = null;
    let profileImage = null;
    if (body.properties) {
      nickname = body.properties.nickname;
      profileImage = body.properties.profile_image;
    }
    return updateOrCreateUser(userId, body.kaccount_email, nickname,
      profileImage);
  }).then((userRecord) => {
    const userId = userRecord.uid;
    console.log(`creating a custom firebase token based on uid ${userId}`);
    return firebaseAdmin.auth().createCustomToken(userId, {
      provider: 'KAKAO'
    });
  })
};

function updateOrCreateUser(userId, email, displayName, photoURL) {
  console.log('updating or creating a firebase user');
  const updateParams = {
    provider: 'KAKAO',
    displayName: displayName,
  };
  if (displayName) {
    updateParams['displayName'] = displayName;
  } else {
    updateParams['displayName'] = email;
  }
  if (photoURL) {
    updateParams['photoURL'] = photoURL;
  }
  console.log(updateParams);
  return firebaseAdmin.auth().updateUser(userId, updateParams)
    .catch((error) => {
      if (error.code === 'auth/user-not-found') {
        updateParams['uid'] = userId;
        if (email) {
          updateParams['email'] = email;
        }
        return firebaseAdmin.auth().createUser(updateParams);
      }
      throw error;
    });
};

function requestMe(kakaoAccessToken) {
  var toss = 'Bearer ' + kakaoAccessToken;
  console.log('Requesting user profile from Kakao API server.');
  console.log("token " + toss);
  return request({
    method: 'GET',
    headers: {
      'Authorization': toss,
      'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
    },
    url: requestMeUrl,
  });

};


(200).end();
    }
});

router.get("/token", (req, res) => {
  const token = req.query.token;
  if (!token) return res.status(400).send({
      error: 'There is no token.'
    })
    .send({
      message: 'Access token is a required parameter.'
    });

  console.log(`Verifying Kakao token: ${token}`);

  createFirebaseToken(token).then((firebaseToken) => {
    console.log(`Returning firebase token to user: ${firebaseToken}`);
    console.log(firebaseToken);
  });
});


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