//Initialize FirebaseApp
const {
  initializeApp,
  cert
} = require('firebase-admin/app');
const serviceAccount = require('../module/firebase/bookworm-f6973-firebase-adminsdk-lzs4b-a45e20e976.json');
const {
  getFirestore,
  Timestamp,
  FieldValue
} = require('firebase-admin/firestore');
initializeApp({
  credential: cert(serviceAccount)
});
const db = getFirestore();
var checkID = async (token) => {
  var ID = token;
  var find = false;
  const snapshot = await db.collection('users').get();
  snapshot.forEach((doc) => {
    if (doc.id == ID) find = true;
  });
  return find;
};


//사용자 탈퇴 시 사용자의 서브컬렉션을 지워야 함. 


//피드 삭제 -> 전달받은 토큰을 기반으로 사용자의 피드 삭제    
var deleteFeed = async (token) => {
  var query = db.collection('feed').where('UserToken', '==', token);
  query.get().then((snapshot) => {
    snapshot.forEach((doc) => {
      doc.ref.delete();
    });
  });
};

var showlist = async (token) => {
  var followerquery = db.collectionGroup('follower').where('token', '==', token);
  var followingquery = db.collectionGroup('following').where('token', '==', token);
  followerquery.get().then((snapshot) => {
    snapshot.forEach((doc) => {
      //유저의 데이터를 삭제한다. 
      doc.ref.delete().then(() => {
        //부모의 Counts를 1씩 감소 시킨다.
        doc.ref.parent.parent.get().then((doc) => {
          var counts = doc.get("UserInfo.followerCounts");
          doc.ref.update("UserInfo.followerCounts", counts - 1);
        });
      });
    });
  });
  followingquery.get().then((snapshot) => {
    snapshot.forEach((doc) => {
      //유저의 데이터를 삭제한다.
      doc.ref.delete()
        .then(() => {
          doc.ref.parent.parent.get()
            .then((doc) => {
              var counts = doc.get("UserInfo.followingCounts");
              doc.ref.update("UserInfo.followingCounts", counts - 1);
            });
        });
    });
  });
  return true;
}

exports.checkID = checkID;
exports.deleteFeed = deleteFeed;
exports.showlist = showlist;