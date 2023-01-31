const express = require('express');
const router = express.Router();
const fs = require('fs');
const file = './server/app-db.json'


router.get('/', function(req, res, next) {
  fs.readFile(file, 'utf8', (err, data) => {
    if (err) {
      console.error(err);
      return;
    }
    res.send(data);
  });
 
});

router.post('/', (req, res) => {
  fs.writeFile(file, JSON.stringify(req.body), err => {
    if (err) {
      console.error(err);
    }
  });
  res.send(JSON.stringify(req.body));
})
module.exports = router;
