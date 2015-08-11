function(event, ...)
	if event == "COMBAT_LOG_EVENT_UNFILTERED" then
		local _, message, _, _, sourceName, _, _, _, destName, _, _, spellId, spellName = ...
		
		local testing = false
		local netherBanishId = WA_netherBanishId
		
		if testing then
			netherBanishId = 41635
		end
		
		if spellId and spellId == netherBanishId then
			if message and message == "SPELL_AURA_APPLIED" then
				return true
			end
			
		end
		
		local ret = false
		
		for i = 1, 15 do
			if WA_wrought_frames[i].focused then
				ret = true
				break;
			end
		end
		
		if not ret then
			for i = 1, 3 do
				if WA_shackle_frames[i].name then
					ret = true
					break
				end
			end
		end
		
		if not ret then
			WA_radar_setRange(40)
		end
		if not ret then
			WA_radar_hide()
			return not ret
		end
	end
end
