-- SCAN FOR SHACKLES

function(event, ...)
    local _, message, _, _, sourceName, _, _, _, destName, _, _, spellId, spellName = ...
    
    if message and message ~= "SPELL_AURA_APPLIED" and message ~= "SPELL_AURA_REMOVED" then
        return
    end
    
    local testing = true
    local netherBanishId = WA_netherBanishId
    
    if WA_radar_locked then
        return
    end
    
    local ids = { }
    ids[184964] = 1 --Shackled Torment
    if testing then
        -- ids[36032] = 1 --Arcane Charge
        ids[6788] = 1 --Weakened Soul
        ids[6673] = 1 --Battle Shout
        ids[774] = 1 -- Rejuvenation
        
        netherBanishId = 41635
    end
    
    if spellId and spellId == netherBanishId then
        if message and message == "SPELL_AURA_REMOVED" then
            for i = 1, 3 do
                if WA_shackle_frames[i].name then
                    WA_trigger()
                    return true
                end
            end
            for i = 1, 15 do
                if WA_wrought_frames[i].focused then
                    WA_trigger()
                    return true
                end
            end
        end
    end
    
    if spellId and ids[spellId] then
        
        if sourceName and not GetUnitName(sourceName) or destName and not GetUnitName(destName) then
            return
        end
        
        if message == "SPELL_AURA_APPLIED" or message == "SPELL_AURA_APPLIED_DOSE" then
            if destName then
                local uX, uY, _, uMap = UnitPosition(destName)
                if uX and uY and uMap then
                    for i = 1, 3 do
                        if not WA_shackle_frames[i].name then
                            WA_shackle_frames[i].name = destName
                            WA_shackle_frames[i].locX = uX
                            WA_shackle_frames[i].locY = uY
                            WA_shackle_frames[i].map = uMap
                            WA_trigger()
                            return true
                        end
                    end
                    WA_trigger()
                    return true
                end
            end
        elseif message == "SPELL_AURA_REMOVED" then
            if destName then
                WA_removeShackle(destName)
                WA_radar_setRange(40)
            end
        end
    end
    
    
    ids = { }
    --ids[186123] = 1 --Wrought Chaos
    ids[185014] = 1 --Focused Chaos
    if testing then
        ids[17] = 1 --Weakened Soul
        --ids[774] = 1 --Rejuvenation
        ids[61295] = 1 --Riptide
        ids[114030] = 1 -- Vigilance
    end
    
    if spellId and ids[spellId] then
        
        if sourceName and not GetUnitName(sourceName) or destName and not GetUnitName(destName) then
            return false
        end
        
        if message == "SPELL_AURA_APPLIED" then
            if sourceName and destName then
                local _, _, _, focusedMap = UnitPosition(destName)
                for i = 1, 15 do
                    if not WA_wrought_frames[i].wrought then
                        WA_wrought_frames[i].wrought = sourceName
                        WA_wrought_frames[i].focused = destName
                        WA_wrought_frames[i].map = focusedMap
                        WA_trigger()
                        return true
                    end
                end
                WA_trigger()
                return true
            end
        elseif message == "SPELL_AURA_REMOVED" then
            if sourceName and destName then
                WA_removeWrought(destName)
            end
        end
    end
end

