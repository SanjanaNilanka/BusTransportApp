/**
 * @file server.js
 * @description Main server file for the Bus Traveler application.
 * @author WE-CSSE-58
 * @version 1.0.0
 * @date October 11, 2023
 */

const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const { PORT } = require('./src/configs/general.config'); //Import the configurations
const { dbConnection } = require('./src/configs/database.config'); // Import the database connection function

const app = express();

app.use(bodyParser.json());
app.use(cors());

const userRoutes = require('./src/routes/userRoutes');
app.use('/user', userRoutes);

const tokenRoutes = require('./src/routes/tokenRoutes');
app.use('/token', tokenRoutes);

dbConnection()
    .then(() => {
        app.listen(PORT, '0.0.0.0', () => {
            console.log(`Server is running on port ${PORT}`);
        });
    })
    .catch((err) => {
        console.error('Database connection error:', err);
    });