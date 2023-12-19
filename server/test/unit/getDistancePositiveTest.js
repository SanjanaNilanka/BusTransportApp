/**
 * @file getDistancePositiveTest.js
 * @description Positive test case to test the getDistance function.
 * @author Perera K.A.S.N.
 * @version 1.0.0
 * @date October 15, 2023
 */

const chai = require('chai');
const chaiHttp = require('chai-http');
const { expect } = chai;

chai.use(chaiHttp);

// Import the function you want to test
const { getDistance } = require('../../src/controllers/tokenController'); // Update with your actual controller path

describe('getDistance', () => {
    it('should calculate road distance for valid inputs', async function() {
        this.timeout(5000);
        chai.request(getDistance)
            .post('/')
            .send({
                getInLoc: '6.926603033950288, 79.86134990864107',
                getOutLoc: ' ',
            })
            .end((err, res) => {
                expect(res).to.have.status(200);
                expect(res.body.success).to.be.true;
                expect(res.body.distance).to.be.a('number');
            });
    });
});