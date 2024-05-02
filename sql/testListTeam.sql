select *
from team t
         left join user_team ut on t.id = ut.teamId
         left join user u on ut.userId = u.id