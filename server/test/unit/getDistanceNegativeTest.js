/**
 * @file getDistanceNegativeTest.js
 * @description Negative test case to test the getDistance function.
 * @author Perera K.A.S.N.
 * @version 1.0.0
 * @date October 15, 2023
 */

const chai = require('chai');
const chaiHttp = require('chai-http');
const { expect } = chai;

chai.use(chaiHttp);

const { getDistance } = require('../../src/controllers/tokenController');

describe('getDistanceWithoutGetInLoc', () => {
    it('should handle missing startLocation input', async function() {
        chai.request(getDistance)
            .post('/')
            .send({
                getOutLoc: '7.2008798828509, 79.87851604587742',
            })
            .end((err, res) => {
                expect(res).to.have.status(400);
                expect(res.body.error).to.equal('Start location is missing.');
            });
    });
});