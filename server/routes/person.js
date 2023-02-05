const express = require('express');
const router = express.Router();
const fs = require('fs');
const jsonFormat = require ('json-format');
const filePath = './server/data/people/'


function getFilePath(personId) {
  return `${filePath}${personId}.json`
}

router.get('/', function(req, res, next) {
  const person = req.query.id;
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
  fs.writeFile(getFilePath(person["person-id"]), jsonFormat(person), err => {
    if (err) {
      console.error(err);
    }
  });
  res.send(JSON.stringify(req.body));
});

router.delete('/', (req, res) => {
  const person = req.body;
  fs.unlink(getFilePath(person["person-id"]), (err) => {
    if (err) {
      console.error(err)
      return
    }
  })
});


module.exports = router;
