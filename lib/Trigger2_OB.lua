function(event, ...)
	if event == "GROUP_ROSTER_UPDATE" or event == "PLAYER_ENTERING_WORLD" then
		if l then
			s()
			d()
			eee()
			return false
		end
	elseif event == "COMBAT_LOG_EVENT_UNFILTERED" then
		local _, message, _, _, sourceName, _, _, _, destName, _, _, spellId, spellName = ...
    
		if message and message ~= "SPELL_AURA_APPLIED" and message ~= "SPELL_AURA_REMOVED" then
			return
		end
		
		local testing = true
		local netherBanishId = j
		
		if n then
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
					if f[i].name then
						fff()
						return true
					end
				end
				for i = 1, 15 do
					if e[i].focused then
						fff()
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
							if not f[i].name then
								f[i].name = destName
								f[i].locX = uX
								f[i].locY = uY
								f[i].map = uMap
								fff()
								return true
							end
						end
						fff()
						return true
					end
				end
			elseif message == "SPELL_AURA_REMOVED" then
				if destName then
					bbb(destName)
					ll_setRange(40)
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
						if not e[i].wrought then
							e[i].wrought = sourceName
							e[i].focused = destName
							e[i].map = focusedMap
							fff()
							return true
						end
					end
					fff()
					return true
				end
			elseif message == "SPELL_AURA_REMOVED" then
				if sourceName and destName then
					a(destName)
				end
			end
		end
	elseif event == "CHAT_MSG_ADDON" or event == "BN_CHAT_MSG_ADDON" then
		bb = bb or { }

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
									if bb[source] then
										return
									end
									bb[source] = 1
									if oo then
										oo(battleTagFrom, source)
									end
								end
							end
						end
					elseif channel == "WHISPER" then
						if message then
							if message == "shutdown" then
								n = true
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
					if bb[sender] then
						return
					end
					bb[sender] = 1
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
						oo(battleTagFrom, sender)
					end
				end
			end
		end

	elseif event == "PLAYER_REGEN_ENABLED" or event == "PLAYER_REGEN_DISABLED" then
		WeakAuras.ScanEvents("PLAYER_ENTERING_WORLD")
		y()
	end
end

