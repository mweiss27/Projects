function()
    
    if WA_radar then
        
        if WA_radar_locked then
            return
        end
        
        WA_radar_updateDots()
        WA_radar_updateShackles()
        WA_radar_updateWrought()
        --WA_radar_testWrought() -- Do NOT uncomment this. The function itself is removed to reduce the size of the WA for distribution
        
    end
    return false
end

