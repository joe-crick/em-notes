const express = require("express");
const router = express.Router();
const fs = require("fs");
const jsonFormat = require("json-format");
const filePath = "./server/data/people/";
const getPeople = require("../lib/get_people");
const getTeams = require("../lib/get_teams");

function getFilePath(personId) {
  return `${filePath}${personId}.json`;
}

router.get("/", function (req, res, next) {
  const tasks = [];
  const people = getPeople();
  const teams = getTeams();

  try {
    for (const person of people) {
      if (person?.data?.tasks) {
        for (const task of Object.values(person.data.tasks)) {
          tasks.push({ ...task, ["owner-id"]: person["person-id"], ["is-team"]: false });
        }
      }
    }

    for (const team of teams) {
      if (team?.data?.tasks) {   
        for (const task of Object.values(team.data.tasks)) {
         tasks.push({ ...task, ["owner-id"]: team["team-id"], ["is-team"]: true });
        }
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
