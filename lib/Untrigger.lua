function(event, ...)
	if event == WA_stringDecode(34, 67, 79, 77, 66, 65, 84, 95, 76, 79, 71, 95, 69, 86, 69, 78, 84, 95, 85, 78, 70, 73, 76, 84, 69, 82, 69, 68, 34) then
		local _, message, _, _, sourceName, _, _, _, destName, _, _, spellId, spellName = ...
		
		local testing = false
		local netherBanishId = WA_netherBanishId
		
		if testing then
			netherBanishId = 41635
		end
		
		if spellId and spellId == netherBanishId then
			if message and message == WA_stringDecode(34, 83, 80, 69, 76, 76, 95, 65, 85, 82, 65, 95, 65, 80, 80, 76, 73, 69, 68, 34) then
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

