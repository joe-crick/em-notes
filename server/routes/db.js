const express = require('express');
const router = express.Router();
const fs = require('fs');
const file = './server/default-db.json'


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
  fs.writeFile(file, req.body, err => {
    if (err) {
      console.error(err);
    }
  });
  res.send(JSON.stringify(data));
})
module.exports = router;
