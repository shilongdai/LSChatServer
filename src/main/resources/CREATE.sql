/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  sdai
 * Created: Nov 29, 2016
 */
CREATE TABLE User(Id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, Username VARCHAR(50) NOT NULL, Credential BINARY(4096));
