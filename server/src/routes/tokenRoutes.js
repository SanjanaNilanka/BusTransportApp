/**
 * @file tokenRoutes.js
 * @description Defines the routes for generating tokens and calculating distances.
 * @author IT21358234_Perera K.A.S.N.
 * @version 1.0.0
 * @date [Date]
 */

const express = require('express');
const tokenController = require('../controllers/tokenController')

// Create an Express router
const router = express.Router();

/**
 * Generate a QR code token for a specific user ID.
 * @route GET /generateToken/:id
 * @group Token - Operations related to tokens
 * @param {string} id.path.required - The ID of the user.
 * @returns {string} 200 - A URL for the generated QR code.
 * @returns {object} 500 - An error response in case of failure.
 */
router.get('/generateToken/:id', tokenController.generateToken);

/**
 * Calculate the distance between two locations.
 * @route POST /getDistance
 * @group Token - Operations related to tokens
 * @param {string} getInLoc.body.required - The starting location.
 * @param {string} getOutLoc.body.required - The destination location.
 * @returns {object} 200 - The calculated distance in kilometers.
 * @returns {object} 500 - An error response in case of failure.
 */
router.post('/getDistance', tokenController.getDistance)

module.exports = router;