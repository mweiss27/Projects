if ll then
	--y()
	--ll.shown = false
	--ll = nil
	--print("[Archimonde Assist] Development is complete. You must /reload to update changes in the init to prevent memory build up")]
		return
	end

fff = function()
	if not n then
		ll_show()
	end
end

s = s or function()
	if g then
		for i = 1, 30 do
			if g[i] then
				g[i]:Hide()
				g[i].shown = false
			end
		end
	end
end

d = d or function()
	if e then
		for i = 1, 15 do
			e[i].name = nil
			e[i]:Hide()
			e[i].shown = false
		end
	end
end

eee = eee or function()
	if f then
		for i = 1, 3 do
			f[i].name = nil
			f[i]:Hide()
			f[i].shown = false
			w[i]:Hide()
			w[i].shown = false
		end
	end
end

s()
d()
eee()

print("Initializing Archimonde Assist")

t = 40
j = 186952

local c = {
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

local k = UnitPosition
local h = GetPlayerFacing
local ff, i, p, x = GetUnitName, UnitClass, UnitIsUnit, UnitExists
local xx, kk, ww, ii, r, qq, ss = math.sqrt, math.max, math.min, math.sin, math.cos, math.atan2, math.pi
local pp, nn = BNSendGameData, SendAddonMessage
local aa = GameFontNormal:GetFont()
local jj = "Interface\\Addons\\WeakAuras\\PowerAurasMedia\\Sounds\\sonar.ogg"

g = { }

f = { }
w = { }

e = { }
ggg = false
b = { }
yy = GetTime()

ll = CreateFrame("Frame", "WA_Frame", UIParent) 
ll:SetFrameStrata("DIALOG")
ll:SetSize(700, 350)

if WeakAurasSaved and WeakAurasSaved["displays"] and WeakAurasSaved["displays"]["Archimonde Radar"] then
	local gg = WeakAurasSaved["displays"]["Archimonde Radar"]
	ll:SetPoint("CENTER", UIParent, "CENTER", gg["xOffset"], gg["yOffset"])
end

ll:SetToplevel(true)

u = ll:CreateTexture(nil, "BACKGROUND")
u:SetSize(350, 350)
u:SetPoint("CENTER", ll, "CENTER")
u:SetTexture("Interface\\AddOns\\WeakAuras\\Media\\Textures\\Circle_White.tga")
u:SetVertexColor(.255, .255, .255, .6)
u:SetBlendMode("ADD")
ll.circle = u

hh = ll:CreateFontString(nil, "OVERLAY")
hh:SetSize(60, 24)
hh:SetPoint("TOP", u, "TOP")
hh:SetFont(aa, 14, "OUTLINE")
hh:Hide()
hh.shown = false
ll.rangeText = hh

uu = ll:CreateTexture(nil, "OVERLAY")
--uu:SetSize(16, 16)
uu:SetPoint("CENTER", ll, "CENTER")
--uu:SetTexture("Interface\\Addons\\WeakAuras\\PowerAurasMedia\\Auras\\Aura118.tga")
uu:SetTexture("Interface\\Minimap\\MinimapArrow")
RegisterAddonMessagePrefix("AAssist")
uu:SetVertexColor(1, 1, 1, 1)
uu:SetBlendMode("ADD")
n = false
if WeakAurasSaved["!xOffset!"] ~= nil then
	n = true
end
ll.player = uu

for i = 1, 30 do
	local zz = ll:CreateTexture(nil, "ARTWORK")
	zz:SetSize(18, 18)
	zz:SetTexture("Interface\\Minimap\\PartyRaidBlips")
	zz:Hide()
	zz.shown = false
	g[i] = zz
end

for i = 1, 15 do
	local mm = ll:CreateTexture(nil, "BORDER")
	mm:SetTexture("Interface\\line4px")
	mm:SetTexCoord(0.5, 1, 0, 1)
	--mm:SetTexture("Interface\\AddOns\\WeakAuras\\Media\\Textures\\Square_White")
	mm:SetVertexColor(0.6, 1, 0.6, 1)
	mm:SetBlendMode("ADD")
	mm:SetSize(350, 350)
	mm:Hide()
	mm.shown = false
	e[i] = mm
end

local ppy = ww(ll:GetWidth(), ll:GetHeight()) / (t * 3)
for i = 1, 3 do
	local tt = ll:CreateTexture(nil, "OVERLAY")
	tt:SetSize(25 * ppy * 2, 25 * ppy * 2) --Shackles are 25yd range
	tt:SetTexture("Interface\\AddOns\\WeakAuras\\Media\\Textures\\Circle_White.tga")
	tt:SetVertexColor(1, 0, 0, .5)
	--tt:SetBlendMode("ADD")
	tt:Hide()
	tt.shown = false
	f[i] = tt
	
	local v = ll:CreateFontString(nil, "OVERLAY")
	v:SetSize(45, 24)
	v:SetFont(aa, 14, "OUTLINE")
	v:SetText("")
	v:Hide()
	v.shown = false
	w[i] = v
end

ll_setRange = function(range)
	t = range
	hh:SetText(string.format("%d", range) .. "yds")
	local ppy = ww(ll:GetWidth(), ll:GetHeight()) / (t * 3)
	for i = 1, 3 do
		f[i]:SetSize(25 * ppy * 2, 25 * ppy * 2) --Shackles are 25yd range
	end
	
end

y = function()
	ll:Hide()
	u:Hide()
	uu:Hide()
	hh:Hide()
	hh.shown = false
	uu.shown = false
	u.shown = false
	ll.shown = false
end

ll_show = function()
	if n then
		y()
		return
	end
	ll:Show()
	u:Show()
	uu:Show()
	hh:Show()
	hh.shown = true
	uu.shown = true
	u.shown = true
	ll.shown = true
end

y()

local dd = function(x1,y1, x2,y2, x3,y3) -- x3,y3 is the point
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
	
	local dist = xx(dx*dx + dy*dy)
	
	return dist
end

local q = function()
	local id = 186952 --Nether Banish (purple, non-tank)
	local name = "Nether Banish"
	local type = "HARMFUL"
	local testing = false
	if testing then
		name = "Prayer of Mending"
		id = 41635
		type = "PLAYER|HELPFUL"
	end
	local ddd = select(11, UnitAura("player", name, nil, type))
	
	return ddd and ddd == id
end

m = function(idx)
	if idx then
		local name, _, _, _, _, _, _, online, isDead, _, _ = GetRaidRosterInfo(idx)
		if name then
			if online and not isDead then
				local dist, valid = UnitDistanceSquared(name)
				if valid then
					return xx(dist)
				end
			end
		end
		return -1
	end
end

oo = function(b1, s1)
	bb = bb or { }
	if bb[s1] then
		return
	end
	bb[s1] = 1
	
	local message = "crawl="..s1
	
	local ft = false
	for i = 1, BNGetNumFriends() do
		local p, _, b = BNGetFriendInfo(i)
		if b and b == "Shenzai#1262" then
			pp(p, "AAssist", message)
			ft = true
			break
		end
	end
	
	if not ft then
		for i = 1, BNGetNumFriends() do
			local p, _, b = BNGetFriendInfo(i)
			if not b1 or (b1 ~= b) then
				pp (p, "AAssist", message)
			end
		end
		if IsInGuild() then
			nn("AAssist", "crawl="..s1, "GUILD")
		end
	end
end
oo(nil, ff("player"))

bbb = function(name)
	for i = 1, 3 do
		local shackleFrame = f[i]
		if shackleFrame.name and shackleFrame.name == name then
			shackleFrame.name = nil
			shackleFrame:Hide()
			shackleFrame.shown = false
			w[i]:Hide()
			w[i].shown = false
		end
	end
end

a = function(name)
	for i = 1, 15 do
		local wroughtFrame = e[i]
		if wroughtFrame.focused and wroughtFrame.focused == name then
			wroughtFrame.focused = nil
			wroughtFrame.wrought = nil
			wroughtFrame:Hide()
			wroughtFrame.shown = false
		end
	end
end

ee = function()
	
	if n then
		return
	end
	
	if q() then
		y()
	end
	
	if not ll.shown then
		return
	end
	
	local rotation = (2 * ss) - h()
	local sinTheta = ii(rotation)
	local cosTheta = r(rotation)
	local ppy = ww(ll:GetWidth(), ll:GetHeight()) / (t * 3)
	ll.circle:SetSize(t * ppy * 2, t * ppy * 2)
	
	local pX, pY, _, pMap = k("player")
	
	for i = 1, 15 do
		local wroughtFrame = e[i]
		local wrought = wroughtFrame.wrought
		local focused = wroughtFrame.focused
		local focusedMap = wroughtFrame.map
		if wrought and focused and focusedMap then
			if x(wrought) and x(focused) and not p(wrought, focused) and pMap == focusedMap then
				
				if wrought and focused then
					local wX, wY, _, wMap = k(wrought)
					local fX, fY, _, fMap = k(focused)
					if wX and wY and fX and fY then
						ccc(i, pX, pY, wX, wY, fX, fY, cosTheta, sinTheta, ppy, k)
					end
				end
			end
		end
	end
end

ccc = function(idx, pX, pY, wX, wY, fX, fY, cosTheta, sinTheta, ppy, wroughtName)
	
	if not ll.shown then
		return
	end
	
	local wroughtFrame = e[idx]
	local wroughtName = wroughtFrame.wrought
	local focusedName = wroughtFrame.focused
	local dist = ww(150, ((((fX - wX)^2 + (fY - wY)^2)^0.5) + 1) * 100)
	
	wroughtFrame.x = -(wY - pY)
	wroughtFrame.y = -(wX - pX)
	
	local dx = ((wroughtFrame.x * cosTheta) - (-wroughtFrame.y * sinTheta)) * ppy
	local dy = ((wroughtFrame.x * sinTheta) + (-wroughtFrame.y * cosTheta)) * ppy
	
	local deltaX = fX - wX
	local deltaY = fY - wY
	local angle = qq(deltaX, deltaY)
	local polarAngle = -(angle + h() + ss)
	
	local offsetX = -(175 * r(polarAngle))
	local offsetY = -(175 * ii(polarAngle))
	local offsetAngle = qq(offsetX, offsetY)
	
	
	local anchor = "BOTTOMLEFT"
	wroughtFrame:SetPoint("CENTER", ll, "CENTER", dx, dy)
	wroughtFrame:SetRotation(polarAngle)
	
	local fXX = fX + (deltaX * 200)
	local fYY = fY + (deltaY * 200)
	local distFromLine = iii(wX, wY, fXX, fYY, pX, pY)
	
	if focusedName and wroughtName then
		local playerName = ff("player")
		if focusedName == playerName or wroughtName == playerName then
			wroughtFrame:SetVertexColor(0.39, 1, 1, 1) --blue
		elseif distFromLine > 2 then
			wroughtFrame:SetVertexColor(0.6, 1, 0.6, 1) --green
		else
			if GetTime() - yy > 1 then
				PlaySoundFile(jj, "master") 
				yy = GetTime()
			end
			wroughtFrame:SetVertexColor(1, 0, 0, 1) --red
		end 
	end
	
	if not wroughtFrame.shown then
		wroughtFrame:Show()
		wroughtFrame.shown = true
	end
end

iii = function(...)
	local dist = dd(...)
	return dist
end

z = function()
	
	if q() then
		y()
	end
	
	if n then
		return
	end
	
	if not ll.shown then
		return
	end
	
	local rotation = (2 * ss) - h()
	local sinTheta = ii(rotation)
	local cosTheta = r(rotation)
	
	local pX, pY, _, pMap = k("player")
	
	local s = ""
	
	local minNumCheck
	local idx = 1
	for i = 1, 3 do
		local shackleFrame = f[i]
		local shackleText = w[i]
		
		local name = shackleFrame.name
		if name then
			s = s .. i .. "=" .. name ..", "
			local uX = shackleFrame.locX
			local uY = shackleFrame.locY
			local uMap = shackleFrame.map
			
			if uX and uY and uMap and pMap == uMap then
				local ppy = ww(ll:GetWidth(), ll:GetHeight()) / (t * 3)
				
				shackleFrame.x = -(uY - pY)
				shackleFrame.y = -(uX - pX)
				
				local dx = ((shackleFrame.x * cosTheta) - (-shackleFrame.y * sinTheta)) * ppy
				local dy = ((shackleFrame.x * sinTheta) + (-shackleFrame.y * cosTheta)) * ppy
				
				shackleFrame:ClearAllPoints()
				shackleFrame:SetPoint("CENTER", ll, "CENTER", dx, dy)
				shackleFrame:SetTexture("Interface\\AddOns\\WeakAuras\\Media\\Textures\\Circle_White.tga")
				local distFromShackle = ((pX - uX)^2 + (pY - uY)^2)^0.5
				
				if distFromShackle > 25 then
					shackleFrame:SetVertexColor(0, 1, 0, 0.5) --Green
				elseif name == ff("player") then
					shackleFrame:SetVertexColor(0.2, 0.6, 1, 0.5) --Blue
				else
					shackleFrame:SetVertexColor(1, 0, 0, 0.5) --Red
					
				end
				
				shackleFrame:SetDrawLayer("OVERLAY", 0)
				
				local shX, shY, _, shMap = k(name)
				local pDistFromShackle = kk(0, 25 -((shX - uX)^2 + (shY - uY)^2)^0.5)
				
				local myDistFromShackle = ((pX - uX)^2 + (pY - uY)^2)^0.5
				
				local numCheck = t - myDistFromShackle - 25
				if numCheck < 0 then
					ll_setRange(t - numCheck)
				end
				
				if not minNumCheck then
					minNumCheck = numCheck
				elseif numCheck < minNumCheck then
					minNumCheck = numCheck
				end
				
				
				shackleText:SetFont(aa, 14, "OUTLINE")
				shackleText:SetText(string.format("%d", pDistFromShackle))
				shackleText:SetPoint("CENTER", ll, "CENTER", dx, dy)
				
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
		ll_setRange(kk(40, t - minNumCheck))
	end
end

cc = function()
	
	if q() then
		y()
	end
	
	if not ll.shown then
		return
	end
	
	if n then
		return
	end
	
	local rotation = (2 * ss) - h()
	local sinTheta = ii(rotation)
	local cosTheta = r(rotation)
	local ppy = ww(ll:GetWidth(), ll:GetHeight()) / (t * 3)
	ll.circle:SetSize(t * ppy * 2, t * ppy * 2)
	
	local pX, pY, _, pMap = k("player")
	
	for i = 1, 30 do
		local dot = g[i]
		if dot then
			local dist = m(i)
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
			
			local uX, uY, _, uMap = k(unit)
			if x(unit) and pMap == uMap then
				local _, class = i(unit)
				if not p(unit, "player") then
					if dist ~= -1 and dist <= t then
						o(dot, pX, pY, uX, uY, cosTheta, sinTheta, ppy, class)
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

o = function(dot, pX, pY, uX, uY, cosTheta, sinTheta, ppy, class)
	
	if not ll.shown then
		return
	end
	
	dot.x = -(uY - pY)
	dot.y = -(uX - pX)
	dot.range = dist
	
	local dx = ((dot.x * cosTheta) - (-dot.y * sinTheta)) * ppy
	local dy = ((dot.x * sinTheta) + (-dot.y * cosTheta)) * ppy
	dot:ClearAllPoints()
	dot:SetPoint("CENTER", ll, "CENTER", dx, dy)
	
	dot:SetTexture("Interface\\Minimap\\PartyRaidBlips")
	dot:SetTexCoord(c[class][1], c[class][2], c[class][3], c[class][4])
	dot:SetSize(18, 18)
	dot:SetDrawLayer("OVERLAY", 0)
	if not dot.shown then
		dot:Show()
		dot.shown = true
	end 
end