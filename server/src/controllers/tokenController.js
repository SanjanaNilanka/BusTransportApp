/**
 * @file tokenController.js
 * @description Implementing the API related to traveling token
 * @author IT21358234_Perera K.A.S.N.
 * @version 1.0.0
 * @date October 11, 2023
 */

const qrcode = require('qrcode')

const { Client } = require('@googlemaps/google-maps-services-js')

const userModel = require('../models/userModel');

// Generate a QR code based on the user's ID
exports.generateToken = async(req, res) => {
    const id = req.params.id;

    // Generate a QR code based on the email
    qrcode.toDataURL(id, (err, url) => {
        if (err) {
            console.error(err);
            res.status(500).json({ error: 'Failed to generate QR code' });
        } else {
            res.send(url);
        }
    });
};

// Calculate the road distance between start and end locations
exports.getDistance = async(req, res) => {
    const client = new Client({});


    let startLocation = req.body.getInLoc
    let endLocation = req.body.getOutLoc

    console.log(`${startLocation}, ${endLocation}`)

    // Define API key here
    const apiKey = 'AIzaSyACx9Htjm7nKi4Vx5Qy9xbi0tl7esyXtb4';

    client
        .directions({
            params: {
                origin: startLocation,
                destination: endLocation,
                key: apiKey,
            },
        })
        .then(response => {
            const route = response.data.routes[0];
            const distance = route.legs.reduce((acc, leg) => acc + leg.distance.value, 0);

            console.log(`Total distance by road: ${distance / 1000} kilometers`);
            return res.send({
                success: true,
                distance: distance,
            });
        })
        .catch(err => {
            console.error('Error calculating road distance:', err);
        });

}