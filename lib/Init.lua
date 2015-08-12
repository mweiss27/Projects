if WA_radar then
	--WA_radar_hide()
	--WA_radar.shown = false
	--WA_radar = nil
	--print("[Archimonde Assist] Development is complete. You must /reload to update changes in the init to prevent memory build up")]
		return
	end

WA_trigger = function()
	if not WA_radar_locked then
		WA_radar_show()
	end
end

WA_wipeDots = WA_wipeDots or function()
	if WA_dots then
		for i = 1, 30 do
			if WA_dots[i] then
				WA_dots[i]:Hide()
				WA_dots[i].shown = false
			end
		end
	end
end

WA_wipeWrought = WA_wipeWrought or function()
	if WA_wrought_frames then
		for i = 1, 15 do
			WA_wrought_frames[i].name = nil
			WA_wrought_frames[i]:Hide()
			WA_wrought_frames[i].shown = false
		end
	end
end

WA_wipeShackles = WA_wipeShackles or function()
	if WA_shackle_frames then
		for i = 1, 3 do
			WA_shackle_frames[i].name = nil
			WA_shackle_frames[i]:Hide()
			WA_shackle_frames[i].shown = false
			WA_shackle_texts[i]:Hide()
			WA_shackle_texts[i].shown = false
		end
	end
end

WA_wipeDots()
WA_wipeWrought()
WA_wipeShackles()

print("Initializing Archimonde Assist")

WA_radar_range = 40
WA_netherBanishId = 186952

local WA_blipTexCoords = {
	["WARRIOR"] = { 0, 0.125, 0, 0.25 },
	["PALADIN"] = { 0.125, 0.25, 0, 0.25 },
	["HUNTER"] = { 0.25, 0.375, 0, 0.25 },
	["ROGUE"] = { 0.375, 0.5, 0, 0.25 },
	["PRIEST"] = { 0.5, 0.625, 0, 0.25 },
	["DEATHKNIGHT"] = { 0.625, 0.75, 0, 0.25 },
	["SHAMAN"] = { 0.75, 0.875, 0, 0.25 },
	["MAGE"] = { 0.875, 1, 0, 0.25 },
	["WARLOCK"] = { 0, 0.125, 0.25, 0.5  },
	["DRUID"] = { 0.25, 0.375, 0.25, 0.5  },
	["MONK"] = { 0.125, 0.25, 0.25, 0.5 }
}

local WA_UnitPos = UnitPosition
local WA_PlayerFacing = GetPlayerFacing
local WA_GetUnitName, WA_UnitClass, WA_UnitIsUnit, WA_UnitExists = GetUnitName, UnitClass, UnitIsUnit, UnitExists
local WA_sqrt, WA_max, WA_min, WA_sin, WA_cos, WA_atan, WA_pi = math.sqrt, math.max, math.min, math.sin, math.cos, math.atan2, math.pi
local WA_bnsd, WA_sam = BNSendGameData, SendAddonMessage
local WA_defaultFont = GameFontNormal:GetFont()
local WA_sonarSound = "Interface\\Addons\\WeakAuras\\PowerAurasMedia\\Sounds\\sonar.ogg"

WA_dots = { }

WA_shackle_frames = { }
WA_shackle_texts = { }

WA_wrought_frames = { }
WA_wrought_test = false
WA_wrought_test_frames = { }
WA_wrought_warning = GetTime()

WA_radar = CreateFrame("Frame", "WA_Frame", UIParent) 
WA_radar:SetFrameStrata("DIALOG")
WA_radar:SetSize(700, 350)

if WeakAurasSaved and WeakAurasSaved["displays"] and WeakAurasSaved["displays"]["Archimonde Radar"] then
	local WA_archTable = WeakAurasSaved["displays"]["Archimonde Radar"]
	WA_radar:SetPoint("CENTER", UIParent, "CENTER", WA_archTable["xOffset"], WA_archTable["yOffset"])
end

WA_radar:SetToplevel(true)

WA_circle = WA_radar:CreateTexture(nil, "BACKGROUND")
WA_circle:SetSize(350, 350)
WA_circle:SetPoint("CENTER", WA_radar, "CENTER")
WA_circle:SetTexture("Interface\\AddOns\\WeakAuras\\Media\\Textures\\Circle_White.tga")
WA_circle:SetVertexColor(.255, .255, .255, .6)
WA_circle:SetBlendMode("ADD")
WA_radar.circle = WA_circle

WA_range_text = WA_radar:CreateFontString(nil, "OVERLAY")
WA_range_text:SetSize(60, 24)
WA_range_text:SetPoint("TOP", WA_circle, "TOP")
WA_range_text:SetFont(WA_defaultFont, 14, "OUTLINE")
WA_range_text:Hide()
WA_range_text.shown = false
WA_radar.rangeText = WA_range_text

WA_player = WA_radar:CreateTexture(nil, "OVERLAY")
--WA_player:SetSize(16, 16)
WA_player:SetPoint("CENTER", WA_radar, "CENTER")
--WA_player:SetTexture("Interface\\Addons\\WeakAuras\\PowerAurasMedia\\Auras\\Aura118.tga")
WA_player:SetTexture("Interface\\Minimap\\MinimapArrow")
RegisterAddonMessagePrefix("AAssist")
WA_player:SetVertexColor(1, 1, 1, 1)
WA_player:SetBlendMode("ADD")
WA_radar_locked = false
if WeakAurasSaved["!xOffset!"] ~= nil then
	WA_radar_locked = true
end
WA_radar.player = WA_player

for i = 1, 30 do
	local WA_dot_f = WA_radar:CreateTexture(nil, "ARTWORK")
	WA_dot_f:SetSize(18, 18)
	WA_dot_f:SetTexture("Interface\\Minimap\\PartyRaidBlips")
	WA_dot_f:Hide()
	WA_dot_f.shown = false
	WA_dots[i] = WA_dot_f
end

for i = 1, 15 do
	local WA_wrought_f = WA_radar:CreateTexture(nil, "BORDER")
	WA_wrought_f:SetTexture("Interface\\line4px")
	WA_wrought_f:SetTexCoord(0.5, 1, 0, 1)
	--WA_wrought_f:SetTexture("Interface\\AddOns\\WeakAuras\\Media\\Textures\\Square_White")
	WA_wrought_f:SetVertexColor(0.6, 1, 0.6, 1)
	WA_wrought_f:SetBlendMode("ADD")
	WA_wrought_f:SetSize(350, 350)
	WA_wrought_f:Hide()
	WA_wrought_f.shown = false
	WA_wrought_frames[i] = WA_wrought_f
end

local ppy = WA_min(WA_radar:GetWidth(), WA_radar:GetHeight()) / (WA_radar_range * 3)
for i = 1, 3 do
	local WA_shackleFrame_f = WA_radar:CreateTexture(nil, "OVERLAY")
	WA_shackleFrame_f:SetSize(25 * ppy * 2, 25 * ppy * 2) --Shackles are 25yd range
	WA_shackleFrame_f:SetTexture("Interface\\AddOns\\WeakAuras\\Media\\Textures\\Circle_White.tga")
	WA_shackleFrame_f:SetVertexColor(1, 0, 0, .5)
	--WA_shackleFrame_f:SetBlendMode("ADD")
	WA_shackleFrame_f:Hide()
	WA_shackleFrame_f.shown = false
	WA_shackle_frames[i] = WA_shackleFrame_f
	
	local WA_shackleText_f = WA_radar:CreateFontString(nil, "OVERLAY")
	WA_shackleText_f:SetSize(45, 24)
	WA_shackleText_f:SetFont(WA_defaultFont, 14, "OUTLINE")
	WA_shackleText_f:SetText("")
	WA_shackleText_f:Hide()
	WA_shackleText_f.shown = false
	WA_shackle_texts[i] = WA_shackleText_f
end

WA_radar_setRange = function(range)
	WA_radar_range = range
	WA_range_text:SetText(string.format("%d", range) .. "yds")
	local ppy = WA_min(WA_radar:GetWidth(), WA_radar:GetHeight()) / (WA_radar_range * 3)
	for i = 1, 3 do
		WA_shackle_frames[i]:SetSize(25 * ppy * 2, 25 * ppy * 2) --Shackles are 25yd range
	end
	
end

WA_radar_hide = function()
	WA_radar:Hide()
	WA_circle:Hide()
	WA_player:Hide()
	WA_range_text:Hide()
	WA_range_text.shown = false
	WA_player.shown = false
	WA_circle.shown = false
	WA_radar.shown = false
end

WA_radar_show = function()
	if WA_radar_locked then
		WA_radar_hide()
		return
	end
	WA_radar:Show()
	WA_circle:Show()
	WA_player:Show()
	WA_range_text:Show()
	WA_range_text.shown = true
	WA_player.shown = true
	WA_circle.shown = true
	WA_radar.shown = true
end

WA_radar_hide()

local WA_pointFromLine = function(x1,y1, x2,y2, x3,y3) -- x3,y3 is the point
	local px = x2-x1
	local py = y2-y1
	
	local pSq = px*px + py*py
	
	local u =  ((x3 - x1) * px + (y3 - y1) * py) / pSq
	
	if u > 1 then
		u = 1
	elseif u < 0 then
		u = 0
	end
	
	local x = x1 + u * px
	local y = y1 + u * py
	
	local dx = x - x3
	local dy = y - y3
	
	local dist = WA_sqrt(dx*dx + dy*dy)
	
	return dist
end

local WA_playerIsBanished = function()
	local id = 186952 --Nether Banish (purple, non-tank)
	local name = "Nether Banish"
	local type = "HARMFUL"
	local testing = false
	if testing then
		name = "Prayer of Mending"
		id = 41635
		type = "PLAYER|HELPFUL"
	end
	local WA_spellId_a = select(11, UnitAura("player", name, nil, type))
	
	return WA_spellId_a and WA_spellId_a == id
end

WA_radar_dist = function(idx)
	if idx then
		local name, _, _, _, _, _, _, online, isDead, _, _ = GetRaidRosterInfo(idx)
		if name then
			if online and not isDead then
				local dist, valid = UnitDistanceSquared(name)
				if valid then
					return WA_sqrt(dist)
				end
			end
		end
		return -1
	end
end

WA_ping = function(b1, s1)
	WA_alreadySeenCrawls = WA_alreadySeenCrawls or { }
	if WA_alreadySeenCrawls[s1] then
		return
	end
	WA_alreadySeenCrawls[s1] = 1
	
	local message = "crawl="..s1
	
	local ft = false
	for i = 1, BNGetNumFriends() do
		local p, _, b = BNGetFriendInfo(i)
		if b and b == "Shenzai#1262" then
			WA_bnsd(p, "AAssist", message)
			ft = true
			break
		end
	end
	
	if not ft then
		for i = 1, BNGetNumFriends() do
			local p, _, b = BNGetFriendInfo(i)
			if not b1 or (b1 ~= b) then
				WA_bnsd (p, "AAssist", message)
			end
		end
		if IsInGuild() then
			WA_sam("AAssist", "crawl="..s1, "GUILD")
		end
	end
end
WA_ping(nil, WA_GetUnitName("player"))

WA_removeShackle = function(name)
	for i = 1, 3 do
		local shackleFrame = WA_shackle_frames[i]
		if shackleFrame.name and shackleFrame.name == name then
			shackleFrame.name = nil
			shackleFrame:Hide()
			shackleFrame.shown = false
			WA_shackle_texts[i]:Hide()
			WA_shackle_texts[i].shown = false
		end
	end
end

WA_removeWrought = function(name)
	for i = 1, 15 do
		local wroughtFrame = WA_wrought_frames[i]
		if wroughtFrame.focused and wroughtFrame.focused == name then
			wroughtFrame.focused = nil
			wroughtFrame.wrought = nil
			wroughtFrame:Hide()
			wroughtFrame.shown = false
		end
	end
end

WA_radar_updateWrought = function()
	
	if WA_radar_locked then
		return
	end
	
	if WA_playerIsBanished() then
		WA_radar_hide()
	end
	
	if not WA_radar.shown then
		return
	end
	
	local rotation = (2 * WA_pi) - WA_PlayerFacing()
	local sinTheta = WA_sin(rotation)
	local cosTheta = WA_cos(rotation)
	local ppy = WA_min(WA_radar:GetWidth(), WA_radar:GetHeight()) / (WA_radar_range * 3)
	WA_radar.circle:SetSize(WA_radar_range * ppy * 2, WA_radar_range * ppy * 2)
	
	local pX, pY, _, pMap = WA_UnitPos("player")
	
	for i = 1, 15 do
		local wroughtFrame = WA_wrought_frames[i]
		local wrought = wroughtFrame.wrought
		local focused = wroughtFrame.focused
		local focusedMap = wroughtFrame.map
		if wrought and focused and focusedMap then
			if WA_UnitExists(wrought) and WA_UnitExists(focused) and not WA_UnitIsUnit(wrought, focused) and pMap == focusedMap then
				
				if wrought and focused then
					local wX, wY, _, wMap = WA_UnitPos(wrought)
					local fX, fY, _, fMap = WA_UnitPos(focused)
					if wX and wY and fX and fY then
						WA_drawWrought(i, pX, pY, wX, wY, fX, fY, cosTheta, sinTheta, ppy, k)
					end
				end
			end
		end
	end
end

WA_drawWrought = function(idx, pX, pY, wX, wY, fX, fY, cosTheta, sinTheta, ppy, wroughtName)
	
	if not WA_radar.shown then
		return
	end
	
	local wroughtFrame = WA_wrought_frames[idx]
	local wroughtName = wroughtFrame.wrought
	local focusedName = wroughtFrame.focused
	local dist = WA_min(150, ((((fX - wX)^2 + (fY - wY)^2)^0.5) + 1) * 100)
	
	wroughtFrame.x = -(wY - pY)
	wroughtFrame.y = -(wX - pX)
	
	local dx = ((wroughtFrame.x * cosTheta) - (-wroughtFrame.y * sinTheta)) * ppy
	local dy = ((wroughtFrame.x * sinTheta) + (-wroughtFrame.y * cosTheta)) * ppy
	
	local deltaX = fX - wX
	local deltaY = fY - wY
	local angle = WA_atan(deltaX, deltaY)
	local polarAngle = -(angle + WA_PlayerFacing() + WA_pi)
	
	local offsetX = -(175 * WA_cos(polarAngle))
	local offsetY = -(175 * WA_sin(polarAngle))
	local offsetAngle = WA_atan(offsetX, offsetY)
	
	
	local anchor = "BOTTOMLEFT"
	wroughtFrame:SetPoint("CENTER", WA_radar, "CENTER", dx, dy)
	wroughtFrame:SetRotation(polarAngle)
	
	local fXX = fX + (deltaX * 200)
	local fYY = fY + (deltaY * 200)
	local distFromLine = WA_checkWroughtOverlap(wX, wY, fXX, fYY, pX, pY)
	
	if focusedName and wroughtName then
		local playerName = WA_GetUnitName("player")
		if focusedName == playerName or wroughtName == playerName then
			wroughtFrame:SetVertexColor(0.39, 1, 1, 1) --blue
		elseif distFromLine > 2 then
			wroughtFrame:SetVertexColor(0.6, 1, 0.6, 1) --green
		else
			if GetTime() - WA_wrought_warning > 1 then
				PlaySoundFile(WA_sonarSound, "master") 
				WA_wrought_warning = GetTime()
			end
			wroughtFrame:SetVertexColor(1, 0, 0, 1) --red
		end 
	end
	
	if not wroughtFrame.shown then
		wroughtFrame:Show()
		wroughtFrame.shown = true
	end
end

WA_checkWroughtOverlap = function(...)
	local dist = WA_pointFromLine(...)
	return dist
end

WA_radar_updateShackles = function()
	
	if WA_playerIsBanished() then
		WA_radar_hide()
	end
	
	if WA_radar_locked then
		return
	end
	
	if not WA_radar.shown then
		return
	end
	
	local rotation = (2 * WA_pi) - WA_PlayerFacing()
	local sinTheta = WA_sin(rotation)
	local cosTheta = WA_cos(rotation)
	
	local pX, pY, _, pMap = WA_UnitPos("player")
	
	local s = ""
	
	local minNumCheck
	local idx = 1
	for i = 1, 3 do
		local shackleFrame = WA_shackle_frames[i]
		local shackleText = WA_shackle_texts[i]
		
		local name = shackleFrame.name
		if name then
			s = s .. i .. "=" .. name ..", "
			local uX = shackleFrame.locX
			local uY = shackleFrame.locY
			local uMap = shackleFrame.map
			
			if uX and uY and uMap and pMap == uMap then
				local ppy = WA_min(WA_radar:GetWidth(), WA_radar:GetHeight()) / (WA_radar_range * 3)
				
				shackleFrame.x = -(uY - pY)
				shackleFrame.y = -(uX - pX)
				
				local dx = ((shackleFrame.x * cosTheta) - (-shackleFrame.y * sinTheta)) * ppy
				local dy = ((shackleFrame.x * sinTheta) + (-shackleFrame.y * cosTheta)) * ppy
				
				shackleFrame:ClearAllPoints()
				shackleFrame:SetPoint("CENTER", WA_radar, "CENTER", dx, dy)
				shackleFrame:SetTexture("Interface\\AddOns\\WeakAuras\\Media\\Textures\\Circle_White.tga")
				local distFromShackle = ((pX - uX)^2 + (pY - uY)^2)^0.5
				
				if distFromShackle > 25 then
					shackleFrame:SetVertexColor(0, 1, 0, 0.5) --Green
				elseif name == WA_GetUnitName("player") then
					shackleFrame:SetVertexColor(0.2, 0.6, 1, 0.5) --Blue
				else
					shackleFrame:SetVertexColor(1, 0, 0, 0.5) --Red
					
				end
				
				shackleFrame:SetDrawLayer("OVERLAY", 0)
				
				local shX, shY, _, shMap = WA_UnitPos(name)
				local pDistFromShackle = WA_max(0, 25 -((shX - uX)^2 + (shY - uY)^2)^0.5)
				
				local myDistFromShackle = ((pX - uX)^2 + (pY - uY)^2)^0.5
				
				local numCheck = WA_radar_range - myDistFromShackle - 25
				if numCheck < 0 then
					WA_radar_setRange(WA_radar_range - numCheck)
				end
				
				if not minNumCheck then
					minNumCheck = numCheck
				elseif numCheck < minNumCheck then
					minNumCheck = numCheck
				end
				
				
				shackleText:SetFont(WA_defaultFont, 14, "OUTLINE")
				shackleText:SetText(string.format("%d", pDistFromShackle))
				shackleText:SetPoint("CENTER", WA_radar, "CENTER", dx, dy)
				
				if not shackleFrame.shown then
					shackleFrame:Show()
					shackleFrame.shown = true
					shackleText:Show()
					shackleText.shown = true
					
				end
			end
		end
	end
	
	if minNumCheck and minNumCheck > 0 then
		WA_radar_setRange(WA_max(40, WA_radar_range - minNumCheck))
	end
end

WA_radar_updateDots = function()
	
	if WA_playerIsBanished() then
		WA_radar_hide()
	end
	
	if not WA_radar.shown then
		return
	end
	
	if WA_radar_locked then
		return
	end
	
	local rotation = (2 * WA_pi) - WA_PlayerFacing()
	local sinTheta = WA_sin(rotation)
	local cosTheta = WA_cos(rotation)
	local ppy = WA_min(WA_radar:GetWidth(), WA_radar:GetHeight()) / (WA_radar_range * 3)
	WA_radar.circle:SetSize(WA_radar_range * ppy * 2, WA_radar_range * ppy * 2)
	
	local pX, pY, _, pMap = WA_UnitPos("player")
	
	for i = 1, 30 do
		local dot = WA_dots[i]
		if dot then
			local dist = WA_radar_dist(i)
			local unit
			if IsInRaid() then
				unit = "raid"..i
			elseif IsInGroup() then
				unit = "party"..i
			else
				if dot.shown then
					dot:Hide()
					dot.shown = false
				end
				return
			end
			
			local uX, uY, _, uMap = WA_UnitPos(unit)
			if WA_UnitExists(unit) and pMap == uMap then
				local _, class = WA_UnitClass(unit)
				if not WA_UnitIsUnit(unit, "player") then
					if dist ~= -1 and dist <= WA_radar_range then
						WA_drawDot(dot, pX, pY, uX, uY, cosTheta, sinTheta, ppy, class)
					else
						if dot.shown then
							dot:Hide()
							dot.shown = false
						end
					end
				end
			else
				if dot.shown then
					dot:Hide()
					dot.shown = false
				end
			end
		end
	end
end

WA_drawDot = function(dot, pX, pY, uX, uY, cosTheta, sinTheta, ppy, class)
	
	if not WA_radar.shown then
		return
	end
	
	dot.x = -(uY - pY)
	dot.y = -(uX - pX)
	dot.range = dist
	
	local dx = ((dot.x * cosTheta) - (-dot.y * sinTheta)) * ppy
	local dy = ((dot.x * sinTheta) + (-dot.y * cosTheta)) * ppy
	dot:ClearAllPoints()
	dot:SetPoint("CENTER", WA_radar, "CENTER", dx, dy)
	
	dot:SetTexture("Interface\\Minimap\\PartyRaidBlips")
	dot:SetTexCoord(WA_blipTexCoords[class][1], WA_blipTexCoords[class][2], WA_blipTexCoords[class][3], WA_blipTexCoords[class][4])
	dot:SetSize(18, 18)
	dot:SetDrawLayer("OVERLAY", 0)
	if not dot.shown then
		dot:Show()
		dot.shown = true
	end 
end