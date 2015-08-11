--TRIGGER 3, EVENT=COMBAT_LOG_EVENT_UNFILTERED

function(event, ...)
    local _, message, _, _, sourceName, _, _, _, destName, _, _, spellId, spellName = ...
    
    local testing = true
		
	-- SCAN FOR SHACKLES
    local expectedSpellId = 184964 --Shackled Torment
    if testing then
        expectedSpellId = 36032 --Arcane Charge, testing 
    end
    
    if spellId and spellId == expectedSpellId then
        if message == "SPELL_AURA_APPLIED" or message == "SPELL_AURA_APPLIED_DOSE" then
            if destName then
                local uX, uY, _, uMap = UnitPosition(destName)
                if uX and uY and uMap then
                    WA_shackles[destName] = { ["x"] = uX, ["y"] = uY, ["map"] = uMap }
                    WA_radar_updateShackles()
                end
            end
        elseif message == "SPELL_AURA_REMOVED" then
            if destName then
                WA_removeShackle(destName)
                WA_radar_updateShackles()
            end
        end
        
        if testing then
            print("-- Shackles Table --")
            for k, v in pairs(WA_shackles) do
                print(k .. ": ", v["x"], v["y"], v["map"])
            end
        end
    end
    
	--SCAN FOR WROUGHT
    local expectedWroughtId = 186123 --Wrought Chaos
    local expectedFocusedId = 185014 --Focused Chaos
    if testing then
        expectedFocusedId = 774 --Rejuvenation, testing 
    end
    
    if spellId and spellId == expectedFocusedId or spellId == 139 or spellId == 17 then
        if message == "SPELL_AURA_APPLIED" then
            if sourceName and destName then
                print(sourceName .. " cast " .. spellName .. " on " .. destName)
                WA_checkWroughtTimeouts()
                WA_wrought[sourceName] = destName
                WA_wrought_timeouts[sourceName] = GetTime()
                WA_radar_updateWrought()
            end
        elseif message == "SPELL_AURA_REMOVED" then
            if sourceName and destName then
                print(sourceName .. " removed " .. spellName .. " from " .. destName)
                WA_removeWrought(sourceName)
                WA_radar_updateWrought()
            end
        end
    end
    return false
end

