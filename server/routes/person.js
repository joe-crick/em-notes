const express = require('express');
const router = express.Router();
const fs = require('fs');
const jsonFormat = require ('json-format');
const filePath = './server/data/people/'


function getFilePath(person) {
  return `${filePath}${person["person-id"]}.json`
}

router.get('/', function(req, res, next) {
  const person = req.body;
  fs.readFile(getFilePath(person), 'utf8', (err, data) => {
    if (err) {
      console.error(err);
      return;
    }
    res.send(data);
  });
 
});

router.post('/', (req, res) => {
  const person = req.body;
  fs.writeFile(getFilePath(person), jsonFormat(person), err => {
    if (err) {
      console.error(err);
    }
  });
  res.send(JSON.stringify(req.body));
})
module.exports = router;
