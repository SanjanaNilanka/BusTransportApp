/**
 * @file userRoutes.js
 * @description Defines the routes for user-related operations.
 * @author IT21358234_Perera K.A.S.N.
 * @version 1.0.0
 * @date [Date]
 */
const express = require('express');
const userControllers = require('../controllers/userController');
const authMiddleware = require('../middlewares/authMiddleware');

const router = express.Router();

/**
 * Register a new user.
 * @route POST /register
 * @group User - Operations related to user registration
 * @param {UserRegistrationDTO.model} userRegistrationDTO.body.required - The user registration data.
 * @returns {object} 200 - A success message.
 * @returns {object} 500 - An error response in case of failure.
 */
router.post('/register', userControllers.userRegister);

/**
 * Log in a user.
 * @route POST /login
 * @group User - Operations related to user login
 * @param {UserLoginDTO.model} userLoginDTO.body.required - The user login data.
 * @returns {object} 200 - A success message and a JWT token.
 * @returns {object} 404 - An error response if the user does not exist.
 * @returns {object} 401 - An error response if the password is invalid.
 * @returns {object} 500 - An error response in case of failure.
 */
router.post('/login', userControllers.userLogin);

/**
 * Get the details of the logged-in user.
 * @route GET /get-loggedin-user
 * @group User - Operations related to the logged-in user
 * @returns {object} 200 - The user's details.
 * @returns {object} 500 - An error response in case of failure.
 */
router.get('/get-loggedin-user', authMiddleware, userControllers.getLoggedinUser);

/**
 * Get a user by their ID.
 * @route GET /getUserById/{id}
 * @group User - Operations related to fetching a user by ID
 * @param {string} id.path.required - The ID of the user.
 * @returns {object} 200 - The user's details.
 * @returns {object} 500 - An error response in case of failure.
 */
router.get('/getUserById/:id', userControllers.getUserById);

/**
 * Get a user by their email address.
 * @route GET /getUserByEmail/{email}
 * @group User - Operations related to fetching a user by email
 * @param {string} email.path.required - The email address of the user.
 * @returns {object} 200 - The user's details.
 * @returns {object} 500 - An error response in case of failure.
 */
router.get('/getUserByEmail/:email', authMiddleware, userControllers.getUserByEmail);

/**
 * Update the user's "getInPunch" location.
 * @route PUT /updateGetInPunch/{id}
 * @group User - Operations related to updating the "getInPunch" location
 * @param {string} id.path.required - The ID of the user.
 * @param {UpdateGetInPunchDTO.model} updateGetInPunchDTO.body.required - The updated "getInPunch" location.
 * @returns {object} 200 - A success message.
 * @returns {object} 500 - An error response in case of failure.
 */
router.put('/updateGetInPunch/:id', userControllers.updateGetInPunch);


/**
 * Update the user's credit and last journey fare.
 * @route PUT /updateCreditFare/{id}
 * @group User - Operations related to updating credit and last journey fare
 * @param {string} id.path.required - The ID of the user.
 * @param {UpdateCreditFareDTO.model} updateCreditFareDTO.body.required - The updated credit and last journey fare.
 * @returns {object} 200 - A success message.
 * @returns {object} 500 - An error response in case of failure.
 */
router.put('/updateCreditFare/:id', userControllers.updateCreditFare);

/**
 * Update the user's journeys.
 * @route PUT /updateJourneys/{userId}
 * @group User - Operations related to updating user journeys
 * @param {string} userId.path.required - The ID of the user.
 * @param {UpdateJourneysDTO.model} updateJourneysDTO.body.required - The new journey data.
 * @returns {object} 200 - A success message.
 * @returns {object} 500 - An error response in case of failure.
 */
router.put('/updateJourneys/:userId', userControllers.updateJourneys);

/**
 * Get the "journeys" array for a specific user.
 * @route GET /getJourneys/{userId}
 * @group User - Operations related to fetching user journeys
 * @param {string} userId.path.required - The ID of the user.
 * @returns {object} 200 - The user's journeys.
 * @returns {object} 404 - An error response if the user is not found.
 * @returns {object} 500 - An error response in case of failure.
 */
router.get('/getJourneys/:userId', userControllers.getJourneys);

module.exports = router;