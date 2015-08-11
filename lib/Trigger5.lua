alreadySeenCrawls = alreadySeenCrawls or { }

if event == "CHAT_MSG_ADDON" then
	local prefix, message, channel, sender = ...
	sender = Ambiguate(sender, "short")
	if sender then
		if sender == GetUnitName("player") then
			print("This CHAT_MSG_ADDON is from ourself. Ignoring it")
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
							if alreadySeenCrawls[source] then
								return
							end
							alreadySeenCrawls[source] = 1
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
			if alreadySeenCrawls[sender] then
				return
			end
			alreadySeenCrawls[sender] = 1
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
			else
				print("Bad presence ID")
			end
		end
	end
end