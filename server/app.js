var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var cors = require('cors');

var indexRouter = require('./routes/index');
var dbRouter = require('./routes/db');
var personRouter = require('./routes/person');
var taskRouter = require('./routes/tasks');
var teamsRouter = require('./routes/teams');

var app = express();

app.use(cors({
  origin: "http://localhost:8280"
}));

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/db', dbRouter);
app.use('/person', personRouter);
app.use('/tasks', taskRouter);
app.use('/teams', teamsRouter);

module.exports = app;
