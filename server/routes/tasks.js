const express = require('express');
const router = express.Router();
const fs = require('fs');
const jsonFormat = require ('json-format');
const filePath = './server/data/people/'


function getFilePath(personId) {
  return `${filePath}${personId}.json`
}

router.get('/', function(req, res, next) {
  const tasks = [];
  const files = fs.readdirSync(filePath);

  try { 

    for (const file of files) {
      const fullPath = `${filePath}/${file}`;
      const data = fs.readFileSync(fullPath, 'utf-8');
      const person = JSON.parse(data);
      for (const task of Object.values(person.data.tasks)) {
        tasks.push({...task, person_id: person["person-id"]});
      }    
    }

    res.send(JSON.stringify(tasks));
  }
  catch (error) { 
    console.log(error);
  }

});

router.post('/', (req, res) => {
  const task = req.body;
  fs.writeFile(getFilePath(task["person-id"]), jsonFormat(person), err => {
    if (err) {
      console.error(err);
    }
  });
  res.send(JSON.stringify(req.body));
});

router.delete('/', (req, res) => {
  const task = req.body;
  fs.unlink(getFilePath(task["person-id"]), (err) => {
    if (err) {
      console.error(err)
      return
    }
  })
});


module.exports = router;
