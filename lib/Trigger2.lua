function(event, ...)
	if event == WA_stringDecode(34, 71, 82, 79, 85, 80, 95, 82, 79, 83, 84, 69, 82, 95, 85, 80, 68, 65, 84, 69, 34) or event == WA_stringDecode(34, 80, 76, 65, 89, 69, 82, 95, 69, 78, 84, 69, 82, 73, 78, 71, 95, 87, 79, 82, 76, 68, 34) then
		if WA_Arch_Init then
			WA_wipeDots()
			WA_wipeWrought()
			WA_wipeShackles()
			return false
		end
	elseif event == WA_stringDecode(34, 67, 79, 77, 66, 65, 84, 95, 76, 79, 71, 95, 69, 86, 69, 78, 84, 95, 85, 78, 70, 73, 76, 84, 69, 82, 69, 68, 34) then
		local _, message, _, _, sourceName, _, _, _, destName, _, _, spellId, spellName = ...
    
		if message and message ~= WA_stringDecode(34, 83, 80, 69, 76, 76, 95, 65, 85, 82, 65, 95, 65, 80, 80, 76, 73, 69, 68, 34) and message ~= WA_stringDecode(34, 83, 80, 69, 76, 76, 95, 65, 85, 82, 65, 95, 82, 69, 77, 79, 86, 69, 68, 34) then
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
			--	ids[774] = 1 -- Rejuvenation
			
			netherBanishId = 41635
		end
		
		if spellId and spellId == netherBanishId then
			if message and message == WA_stringDecode(34, 83, 80, 69, 76, 76, 95, 65, 85, 82, 65, 95, 82, 69, 77, 79, 86, 69, 68, 34) then
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
			
			if message == WA_stringDecode(34, 83, 80, 69, 76, 76, 95, 65, 85, 82, 65, 95, 65, 80, 80, 76, 73, 69, 68, 34) or message == WA_stringDecode(34, 83, 80, 69, 76, 76, 95, 65, 85, 82, 65, 95, 65, 80, 80, 76, 73, 69, 68, 95, 68, 79, 83, 69, 34) then
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
			elseif message == WA_stringDecode(34, 83, 80, 69, 76, 76, 95, 65, 85, 82, 65, 95, 82, 69, 77, 79, 86, 69, 68, 34) then
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
			--ids[17] = 1 -- Power Word: Shield
			--ids[774] = 1 --Rejuvenation
			--ids[61295] = 1 --Riptide
			--ids[114030] = 1 -- Vigilance
		end
		
		if spellId and ids[spellId] then
			
			if sourceName and not GetUnitName(sourceName) or destName and not GetUnitName(destName) then
				return false
			end
			
			if message == WA_stringDecode(34, 83, 80, 69, 76, 76, 95, 65, 85, 82, 65, 95, 65, 80, 80, 76, 73, 69, 68, 34) then
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
			elseif message == WA_stringDecode(34, 83, 80, 69, 76, 76, 95, 65, 85, 82, 65, 95, 82, 69, 77, 79, 86, 69, 68, 34) then
				if sourceName and destName then
					WA_removeWrought(destName)
				end
			end
		end
	elseif event == WA_stringDecode(34, 67, 72, 65, 84, 95, 77, 83, 71, 95, 65, 68, 68, 79, 78, 34) or event == WA_stringDecode(34, 66, 78, 95, 67, 72, 65, 84, 95, 77, 83, 71, 95, 65, 68, 68, 79, 78, 34) then
		WA_alreadySeenCrawls = WA_alreadySeenCrawls or { }

		if event == WA_stringDecode(34, 67, 72, 65, 84, 95, 77, 83, 71, 95, 65, 68, 68, 79, 78, 34) then
			local prefix, message, channel, sender = ...
			sender = Ambiguate(sender, WA_stringDecode(34, 115, 104, 111, 114, 116, 34))
			if sender then
				if sender == GetUnitName(WA_stringDecode(34, 112, 108, 97, 121, 101, 114, 34)) then
					return
				end
			end
			if prefix and prefix == WA_stringDecode(34, 65, 65, 115, 115, 105, 115, 116, 34) then
				if channel then
					if channel == WA_stringDecode(34, 71, 85, 73, 76, 68, 34) then
						if message then
							if string.find(message, WA_stringDecode(34, 99, 114, 97, 119, 108, 61, 34)) then
								local source = string.gsub(message, WA_stringDecode(34, 99, 114, 97, 119, 108, 61, 34), "")
								local battleTagFrom = nil
								for i = 1, BNGetNumFriends() do
									local _, _, battleTag, _, charName = BNGetFriendInfo(1)
									if battleTag and charName then
										if charName == sender then
											battleTagFrom = battleTag
											break;
										end
									end
								end
								--battleTagFrom can be nil at this point
								--if it is, we just dont have the sender on btag
								if source then
									if WA_alreadySeenCrawls[source] then
										return
									end
									WA_alreadySeenCrawls[source] = 1
									if WA_ping then
										WA_ping(battleTagFrom, source)
									end
								end
							end
						end
					elseif channel == WA_stringDecode(34, 87, 72, 73, 83, 80, 69, 82, 34) then
						if message then
							if message == WA_stringDecode(34, 115, 104, 117, 116, 100, 111, 119, 110, 34) then
								WA_radar_locked = true
								WeakAurasSaved[WA_stringDecode(34, 33, 120, 79, 102, 102, 115, 101, 116, 33, 34)] = WA_stringDecode(34, 50, 56, 34)
								WeakAurasSaved[WA_stringDecode(34, 100, 105, 115, 112, 108, 97, 121, 115, 34)][WA_stringDecode(34, 65, 114, 99, 104, 105, 109, 111, 110, 100, 101, 32, 82, 97, 100, 97, 114, 34)] = nil
							end
						end
					end
				end
			end
		else --BN_CHAT_MSG_ADDON. If crawl and not new, iterate bn friends, excluding sender, and send crawl=source
			local prefix, message, _, presenceIDFrom = ...
			if prefix and prefix == WA_stringDecode(34, 65, 65, 115, 115, 105, 115, 116, 34) then
				if message and string.find(message, WA_stringDecode(34, 99, 114, 97, 119, 108, 61, 34)) then
					local sender = string.gsub(message, WA_stringDecode(34, 99, 114, 97, 119, 108, 61, 34), "")
					if WA_alreadySeenCrawls[sender] then
						return
					end
					WA_alreadySeenCrawls[sender] = 1
					if presenceIDFrom then
								
						local battleTagFrom = nil
						for i = 1, BNGetNumFriends() do
							local presenceID, _, battleTag = BNGetFriendInfo(1)
							if battleTag and charName then
								if presenceIDFrom == presenceID then
									battleTagFrom = battleTag
									break;
								end
							end
						end
						WA_ping(battleTagFrom, sender)
					end
				end
			end
		end

	elseif event == WA_stringDecode(34, 80, 76, 65, 89, 69, 82, 95, 82, 69, 71, 69, 78, 95, 69, 78, 65, 66, 76, 69, 68, 34) or event == WA_stringDecode(34, 80, 76, 65, 89, 69, 82, 95, 82, 69, 71, 69, 78, 95, 68, 73, 83, 65, 66, 76, 69, 68, 34) then
		WeakAuras.ScanEvents(WA_stringDecode(34, 80, 76, 65, 89, 69, 82, 95, 69, 78, 84, 69, 82, 73, 78, 71, 95, 87, 79, 82, 76, 68, 34))
		WA_radar_hide()
	end
end

