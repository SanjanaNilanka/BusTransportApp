/**
 * @file userModel.js
 * @description Defines the Mongoose schema for the "users" collection.
 * @author IT21358234_Perera K.A.S.N.
 * @version 1.0.0
 * @date [Date]
 */

const mongoose = require("mongoose");


/**
 * Mongoose schema for user documents in the "users" collection.
 */
const userSchema = new mongoose.Schema({
    firstName: {
        type: String,
        required: true,
    },
    lastName: {
        type: String,
        required: true,
    },
    emailAddress: {
        type: String,
        required: true,
        unique: true,
    },
    phoneNumber: {
        type: String,
        required: false,
        default: "",
    },
    password: {
        type: String,
        required: true,
    },
    role: {
        type: String,
        required: false,
        default: "passenger",
    },
    tokenURL: {
        type: String,
        required: false,
        default: "",
    },
    getInLoc: {
        type: String,
        required: false,
        default: "",
    },
    credit: {
        type: Number,
        required: false,
        default: 500,
    },
    lastJourneyFare: {
        type: Number,
        required: false,
        default: 0,
    },
    journeys: [{
        startLocation: String,
        endLocation: String,
        duration: Number,
        fare: Number,
        date: { type: Date, default: Date.now }
    }]
});

/**
 * Mongoose model for the "users" collection.
 */
module.exports = mongoose.model("users", userSchema);