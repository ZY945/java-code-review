local key = KEYS[1]
local limit = tonumber(ARGV[1])
local expire_time = ARGV[2]

local current = tonumber(redis.call('get', key) or "0")
if current + 1 > limit then
    return 0
else
    redis.call("INCRBY", key, 1)
    redis.call("EXPIRE", key, expire_time)
    return 1
end 