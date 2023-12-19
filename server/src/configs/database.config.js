/**
 * @file database.config.js
 * @description database connection and configurations.
 * @author IT21358234_Perera K.A.S.N.
 * @version 1.0.0
 * @date October 11, 2023
 */

require('dotenv').config();
const mongoose = require('mongoose');
const { DB_URL } = require('./general.config'); // Import the database URL

/**
 * Establish a database connection to the MongoDB database using Mongoose.
 * This function returns a promise to handle the connection process.
 *
 * @returns {Promise} A promise that resolves when the connection is established or rejects on error.
 */
const dbConnection = () => {
    return new Promise((resolve, reject) => {
        mongoose.connect(DB_URL, {
                useNewUrlParser: true,
                useUnifiedTopology: true,
            })
            .then(() => {
                console.log('Database connection established.');
                resolve();
            })
            // Handle database connection error
            .catch((err) => {
                console.error('Database connection error:', err);
                reject(err);
            });
    });
};

module.exports = { dbConnection };