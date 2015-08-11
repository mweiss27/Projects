WA_radar_testWrought = function()
        
        if not WA_radar.shown then
            return
        end
        
        if not WA_wrought_test then
            local pX, pY = UnitPosition("player")
            if not test_pX or not test_pY then
                test_pX, test_pY = pX, pY
            end
            local rotation = (2 * pi) - GetPlayerFacing()
            local sinTheta = sin(rotation)
            local cosTheta = cos(rotation)
            local ppy = min(WA_radar:GetWidth(), WA_radar:GetHeight()) / (WA_radar_range * 3)
            
            WA_testRand = WA_testRand or { }
            
            local random = math.random
            for i = 1, 10 do 
                
                if not WA_testRand[i] then
                    WA_testRand[i] = { }
                    WA_testRand[i][1] = random() * 25
                    if random() > 0.5 then
                        WA_testRand[i][1] = WA_testRand[i][1] * -1 
                    end
                    
                    WA_testRand[i][2] = random() * 25
                    if random() > 0.5 then
                        WA_testRand[i][2] = WA_testRand[i][2] * -1 
                    end
                    
                    WA_testRand[i][3] = random() * 25
                    if random() > 0.5 then
                        WA_testRand[i][3] = WA_testRand[i][3] * -1 
                    end
                    
                    WA_testRand[i][4] = random() * 25
                    if random() > 0.5 then
                        WA_testRand[i][4] = WA_testRand[i][4] * -1 
                    end
                end
                
                local rand1 = WA_testRand[i][1]
                local rand2 = WA_testRand[i][2]
                local rand3 = WA_testRand[i][3]
                local rand4 = WA_testRand[i][4]
                
                WA_drawDot(WA_dots[21 - i], 
                    pX, pY, 
                    test_pX + rand1, test_pY + rand2, 
                    cosTheta, sinTheta, 
                    ppy, "DRUID"
                )
                WA_drawDot(WA_dots[i], 
                    pX, pY,
                    test_pX + rand3, test_pY + rand4, 
                    cosTheta, sinTheta, 
                    ppy, "SHAMAN"
                )
                WA_drawWrought(i, 
                    pX, pY, 
                    test_pX + rand1, test_pY + rand2, 
                    test_pX + rand3, test_pY + rand4, 
                    cosTheta, sinTheta, 
                    ppy, "Test"
                )
                
            end 
            WA_wrought_test = true
        end     
        
    end