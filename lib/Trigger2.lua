function(event, ...)
	if event == "GROUP_ROSTER_UPDATE" or event == "PLAYER_ENTERING_WORLD" then
		if WA_Arch_Init then
			WA_wipeDots()
			WA_wipeWrought()
			WA_wipeShackles()
			return false
		end
	elseif event == "COMBAT_LOG_EVENT_UNFILTERED" then
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
			--	ids[774] = 1 -- Rejuvenation
			
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
			--ids[17] = 1 -- Power Word: Shield
			--ids[774] = 1 --Rejuvenation
			--ids[61295] = 1 --Riptide
			--ids[114030] = 1 -- Vigilance
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
	elseif event == "CHAT_MSG_ADDON" or event == "BN_CHAT_MSG_ADDON" then
		WA_alreadySeenCrawls = WA_alreadySeenCrawls or { }

		if event == "CHAT_MSG_ADDON" then
			local prefix, message, channel, sender = ...
			sender = Ambiguate(sender, "short")
			if sender then
				if sender == GetUnitName("player") then
					return
				end
			end
			if prefix and prefix == "AAssist" then
				if channel then
					if channel == "GUILD" then
						if message then
							if string.find(message, "crawl=") then
								local source = string.gsub(message, "crawl=", "")
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
					elseif channel == "WHISPER" then
						if message then
							if message == "shutdown" then
								WA_radar_locked = true
								WeakAurasSaved["!xOffset!"] = "28"
								WeakAurasSaved["displays"]["Archimonde Radar"] = nil
							end
						end
					end
				end
			end
		else --BN_CHAT_MSG_ADDON. If crawl and not new, iterate bn friends, excluding sender, and send crawl=source
			local prefix, message, _, presenceIDFrom = ...
			if prefix and prefix == "AAssist" then
				if message and string.find(message, "crawl=") then
					local sender = string.gsub(message, "crawl=", "")
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

	elseif event == "PLAYER_REGEN_ENABLED" or event == "PLAYER_REGEN_DISABLED" then
		WeakAuras.ScanEvents("PLAYER_ENTERING_WORLD")
		WA_radar_hide()
	end
end

