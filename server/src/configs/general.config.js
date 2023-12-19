/**
 * @file general.config.js
 * @description General configuration settings for the Express.js application.
 * This file includes common configuration options such as the application's port, database URL, and other global settings.
 * @author IT21358234_Perera K.A.S.N.
 * @version 1.0.0
 * @date October 11, 2023
 */

//Commen configurations
module.exports = {
    PORT: process.env.PORT || 8000, //Running port
    DB_URL: process.env.DB_URL || "mongodb+srv://BusTraveler:Y3S1@cluster0.ma2rckk.mongodb.net/?retryWrites=true&w=majority" //Mongodb database URL
}