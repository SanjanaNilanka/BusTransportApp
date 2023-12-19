/**
 * @file authMiddleware.js
 * @description Middleware for verifying and decoding JWT tokens.
 * @author IT21358234_Perera K.A.S.N.
 * @version 1.0.0
 * @date [Date]
 */
const jwt = require("jsonwebtoken");

/**
 * Middleware for verifying and decoding JWT tokens.
 *
 * This middleware checks the provided JWT token in the request headers and verifies its authenticity.
 * If the token is valid, it decodes the user ID from the token and attaches it to the request object for further processing.
 *
 * @param {Request} req - Express Request object.
 * @param {Response} res - Express Response object.
 * @param {function} next - The next middleware function in the request-response cycle.
 */
module.exports = function(req, res, next) {
    try {
        // Extract and verify the JWT token from the Authorization header
        const token = req.headers.authorization.split(" ")[1];
        const decoded = jwt.verify(token, process.env.jwt_secret);
        if (decoded.userId) {
            // Attach the decoded user ID to the request object

            req.body.userIdFromToken = decoded.userId;
            next();
        } else {
            // Handle invalid token
            return res.send({
                success: false,
                message: "Invalid token",
            });
        }
    } catch (error) {
        // Handle errors during token verification
        return res.send({
            success: false,
            message: error.message,
        });
    }
};