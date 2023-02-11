const express = require('express');
const router = express.Router();
const fs = require('fs');
const jsonFormat = require ('json-format');
const file = './server/data/app-db.json';
const getPeopleForTeam = require("../lib/get_people_team");


router.get('/', function(req, res, next) {

  fs.readFile(file, 'utf8', (err, data) => {
    if (err) {
      console.error(err);
      return;
    }
    const db = JSON.parse(data);
    const newTeams = Object.entries(db.teams).reduce((acc, [key, value]) => {
      const people = getPeopleForTeam(key);
      value.people = people;
      return {...acc, [key]: value}
    }, {})
    db.teams = newTeams;
    res.send(JSON.stringify(db));
  });
 
});

router.post('/', (req, res) => {
  fs.writeFile(file, jsonFormat(req.body), err => {
    if (err) {
      console.error(err);
    }
  });
  res.send(JSON.stringify(req.body));
})
module.exports = router;
