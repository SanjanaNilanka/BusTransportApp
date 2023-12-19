/**
 * @file userController.js
 * @description Implementing the API related to user handling.
 * @author IT21358234_Perera K.A.S.N.
 * @version 1.0.0
 * @date October 11, 2023
 */

// Import required modules and dependencies
const userModel = require('../models/userModel');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const authMiddleware = require('../middlewares/authMiddleware');


/**
 * Register a new user.
 *
 * @param {Request} req - Express Request object.
 * @param {Response} res - Express Response object.
 */
exports.userRegister = async(req, res) => {
    console.log("Received a registration request:", req.body);
    try {
        // Check if user already exists
        const user = await userModel.findOne({ emailAddress: req.body.email });
        if (user) {
            return res.send({
                success: false,
                message: "Email already exists",
            });
        }

        // Hash password
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(req.body.password, salt);
        req.body.password = hashedPassword;

        // Create new user
        const newUser = new userModel(req.body);
        await newUser.save();

        return res.send({
            success: true,
            message: "User created successfully, please login",
        });
    } catch (error) {
        return res.send({
            success: false,
            message: error.message,
        });
    }
};

/**
 * Login a user.
 *
 * @param {Request} req - Express Request object.
 * @param {Response} res - Express Response object.
 */
exports.userLogin = async(req, res) => {
    console.log("Received a registration request:", req.body);
    try {
        const { emailAddress, password } = req.body;

        // Check if user exists
        const user = await userModel.findOne({ emailAddress });
        if (!user) {
            console.error('not existed')
            return res.status(404).send({
                success: false,
                message: 'User does not exist',
            });

        }

        // Check if password is correct
        const validPassword = await bcrypt.compare(password, user.password);

        if (!validPassword) {
            return res.status(401).send({
                success: false,
                message: 'Invalid password',
            });
            console.error('invalid')
        }

        // create and assign a token
        const token = jwt.sign({ userId: user._id }, process.env.jwt_secret, {
            expiresIn: "1d",
        });
        console.log("success");

        return res.send({
            success: true,
            message: 'Login successful',
            data: token,
            userId: user._id
        });
    } catch (error) {
        console.error('Login Error:', error);
        return res.status(500).send({
            success: false,
            message: 'Internal Server Error',
        });
    }
};

/**
 * Get details of the logged-in user.
 *
 * @param {Request} req - Express Request object with userIdFromToken.
 * @param {Response} res - Express Response object.
 */
exports.getLoggedinUser = async(req, res) => {
    try {
        const user = await userModel.findById(req.body.userIdFromToken);
        if (!user) {
            return res.send({
                success: false,
                message: "User does not exist",
            });
        }
        return res.send({
            success: true,
            message: "User details fetched successfully",
            data: user,
        });
    } catch (error) {
        return res.send({
            success: false,
            message: error.message,
        });
    }

};

/**
 * Get user details by user ID.
 *
 * @param {Request} req - Express Request object with user ID parameter.
 * @param {Response} res - Express Response object.
 */
exports.getUserById = async(req, res) => {
    try {
        const user = await userModel.findById(req.params.id);
        if (!user) {
            return res.send({
                success: false,
                message: "User does not exist",
            });
        }
        return res.send({
            success: true,
            message: "User fetched successfully",
            data: user,
        });

    } catch (error) {
        return res.send({
            success: false,
            message: 'User does not exist',
        });
    }
};

/**
 * Get user details by email address.
 *
 * @param {Request} req - Express Request object with email parameter.
 * @param {Response} res - Express Response object.
 */
exports.getUserByEmail = async(req, res) => {
    try {
        const email = req.params.email; // Assuming the email is passed as a parameter

        const user = await userModel.findOne({ emailAddress: email }); // Assuming the email field in your model is named 'emailAddress'

        if (!user) {
            return res.send({
                success: false,
                message: "User does not exist",
            });
        }

        return res.send({
            success: true,
            message: "User fetched successfully",
            data: user,
        });
    } catch (error) {
        return res.send({
            success: false,
            message: 'An error occurred while fetching the user',
        });
    }
};

/**
 * Update the "get in" punch location for a user.
 *
 * @param {Request} req - Express Request object with user ID parameter and getInLoc in the request body.
 * @param {Response} res - Express Response object.
 */
exports.updateGetInPunch = async(req, res) => {
    try {
        const id = req.params.id;
        const { getInLoc } = req.body;

        userModel.findByIdAndUpdate(id, { getInLoc }, { new: true }).then(() => {
            return res.send({
                success: true,
                message: "Get in location updated",
            });
        });

    } catch (error) {
        return res.send({
            success: false,
            message: "Failed to update",
        });
    }
}

/**
 * Update user's credit and last journey fare.
 *
 * @param {Request} req - Express Request object with user ID parameter and credit and lastJourneyFare in the request body.
 * @param {Response} res - Express Response object.
 */
exports.updateCreditFare = async(req, res) => {
    try {
        const id = req.params.id;
        const { credit, lastJourneyFare } = req.body;

        userModel.findByIdAndUpdate(id, { credit, lastJourneyFare }, { new: true }).then(() => {
            return res.send({
                success: true,
                message: "credit updated",
            });
        });

    } catch (error) {
        return res.send({
            success: false,
            message: "Failed to update credit",
        });
    }
}



/**
 * Retrieve the "journeys" array for a specific user.
 *
 * @param {Request} req - Express Request object with user ID parameter.
 * @param {Response} res - Express Response object.
 */
exports.getJourneys = async(req, res) => {
    try {
        const userId = req.params.userId; // Extract the user ID from the URL

        // Find the user document by ID
        const user = await userModel.findById(userId);

        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }

        // Retrieve and send the "journeys" array
        const journeys = user.journeys;

        return res.status(200).json({ journeys });
    } catch (error) {
        console.error(error);
        return res.status(500).json({ message: 'Internal Server Error' });
    }
};


/**
 * Update the "journeys" array in the user document.
 *
 * @param {Request} req - Express Request object with user ID parameter and new journey data in the request body.
 * @param {Response} res - Express Response object.
 */
exports.updateJourneys = async(req, res) => {
    try {
        const userId = req.params.userId; // Extract the user ID from the URL
        const newJourney = req.body; // The new journey data to push into the array
        console.log('Api called')
            // Find the user document by ID
        const user = await userModel.findById(userId);
        const lastJourneyFare = newJourney.fare
        const credit = user.credit - lastJourneyFare


        await userModel.findByIdAndUpdate(userId, { credit, lastJourneyFare }, { new: true })

        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }

        // Push the new journey data into the "journeys" array
        user.journeys.push(newJourney);

        // Save the updated user document
        await user.save();

        return res.status(200).json({ success: true, message: 'Journey added successfully' });
    } catch (error) {
        console.error(error);
        return res.status(500).json({ success: false, message: 'Internal Server Error' });
    }
};