const express = require("express");
const router = express.Router();
const fs = require("fs");
const jsonFormat = require("json-format");
const filePath = "./server/data/people/";
const getPeople = require("../lib/get_people");

function getFilePath(personId) {
  return `${filePath}${personId}.json`;
}

router.get("/", function (req, res, next) {
  const tasks = [];
  const people = getPeople();

  try {
    for (const person of people) {
      for (const task of Object.values(person.data.tasks)) {
        tasks.push({ ...task, ["person-id"]: person["person-id"] });
      }
    }

    res.send(JSON.stringify(tasks));
  } catch (error) {
    console.log(error);
  }
});

router.post("/", (req, res) => {
  const task = req.body;
  fs.writeFile(getFilePath(task["person-id"]), jsonFormat(person), (err) => {
    if (err) {
      console.error(err);
    }
  });
  res.send(JSON.stringify(req.body));
});

router.delete("/", (req, res) => {
  const task = req.body;
  fs.unlink(getFilePath(task["person-id"]), (err) => {
    if (err) {
      console.error(err);
      return;
    }
  });
});

module.exports = router;
