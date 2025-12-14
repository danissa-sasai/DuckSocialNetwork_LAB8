    package org.example.service;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import org.example.domain.Friendship;
    import org.example.domain.Person;
    import org.example.domain.User;
    import org.example.domain.duck.Duck;
    import org.example.domain.duck.DuckType;
    import org.example.domain.duck.FlyingDuck;
    import org.example.domain.duck.SwimmingDuck;
    import org.example.domain.event.Event;
    import org.example.domain.event.RaceEvent;
    import org.example.domain.flock.Flock;
    import org.example.domain.flock.FlyingFlock;
    import org.example.domain.flock.SwimmingFlock;
    import org.example.domain.message.Message;
    import org.example.dto.DuckFilterDTO;
    import org.example.dto.PersonFilterDTO;
    import org.example.exceptions.UserException;
    import org.example.repo.DB.DBFlockRepo;
    import org.example.repo.DB.DBMessageRepo;
    import org.example.repo.DB.DBUserRepo;
    import org.example.repo.PagingRepo;
    import org.example.repo.Repo;
    import org.example.utils.Pair;
    import org.example.utils.paging.Page;
    import org.example.utils.paging.Pageable;
    import org.example.validators.Validator;

    import java.sql.SQLException;
    import java.util.*;

    public class SocialNetworkService implements Service{
        private final DBUserRepo repoUsers;
        private final PagingRepo<Friendship> repoFriendships;
        private final Repo<Flock<? extends Duck>> repoFlocks;
        private final Repo<Event> repoEvents;
        private final Repo<Message> repoMessages;

        private final Validator<User> userValidator;
        private final Validator<Friendship> friendshipValidator;

        //private ObservableList<Message> messages = FXCollections.observableArrayList();

        /**
         * SocialNetworkService Constructor
         * @param repoUsers - User repository
         * @param repoFriendships - Friendship repository
         * @param userValidator - User validator
         * @param friendshipValidator - Friendship validator
         */
        public SocialNetworkService(DBUserRepo repoUsers, PagingRepo<Friendship> repoFriendships, Repo<Flock<? extends Duck>> repoFlocks,
                                    Repo<Event> repoEvents, Repo<Message> repoMessages,
                                    Validator<User> userValidator, Validator<Friendship> friendshipValidator) {
            if (repoUsers == null || repoFriendships == null || repoFlocks == null || repoEvents == null) {
                throw new IllegalArgumentException("One or more dependencies are null!");
            }

            this.repoUsers = repoUsers;
            this.repoFriendships = repoFriendships;
            this.repoFlocks = repoFlocks;
            this.repoEvents = repoEvents;
            this.repoMessages = repoMessages;

            this.userValidator = userValidator;
            this.friendshipValidator = friendshipValidator;
        }

        @Override
        public void addUser(User user) {
            try {
                userValidator.validate(user);
            }
            catch (UserException userException) {
                return;
            }
            repoUsers.add(user);
        }

        @Override
        public void removeUser(User user) {
            Long userId = user.getId();

            repoUsers.delete(userId);

            //ca sa curat din flockuri
            if (user instanceof Duck duck) {
                for (Flock<?> flock : repoFlocks.getAll()) {
                    if (flock.getMembers().contains(duck)) {
                        ((Flock<Duck>) flock).removeMember(duck);
                    }
                }
            }

            //ca sa curat din raceeventuri
            if (user instanceof SwimmingDuck swimmingDuck) {
                for (Event e : repoEvents.getAll()) {
                    if (e instanceof RaceEvent raceEvent) {
                        if (raceEvent.getParticipants().contains(swimmingDuck)) {
                            raceEvent.getParticipants().remove(swimmingDuck);
                        }
                    }
                }
            }


        }

        @Override
        public void addFriend(Long id1, Long id2) {
            User u1 = repoUsers.getById(id1);
            User u2 = repoUsers.getById(id2);

            if (u1 == null || u2 == null)
                throw new UserException("Both users must exist before adding friendship.");

            Friendship friendship = new Friendship(id1, id2);
            friendshipValidator.validate(friendship);

            boolean exists = repoFriendships.getAll().stream()
                    .anyMatch(f -> f.equals(friendship));
            if (exists)
                throw new UserException("Users are already friends.");

            repoFriendships.add(friendship);
        }

        @Override
        public void removeFriend(Long id1, Long id2) {
            Friendship friendship = repoFriendships.getAll().stream()
                    .filter(f -> (f.getUser1().equals(id1) && f.getUser2().equals(id2)) ||
                            (f.getUser1().equals(id2) && f.getUser2().equals(id1)))
                    .findFirst()
                    .orElseThrow(() -> new UserException("Friendship not found."));

            repoFriendships.delete(friendship.getId());
        }

        @Override
        public List<Friendship> getAllFriendships() {
            return repoFriendships.getAll();
        }

        @Override
        public List<User> getAllUsers() {
            return repoUsers.getAll();
        }

        /// ------------------------------------------
        private Set<User> getFriendsOf(User user) {
            Long id = user.getId();

            Set<User> friends = new HashSet<>();

            for (Friendship f : repoFriendships.getAll()) {
                if (f.getUser1().equals(id)) {
                    friends.add(repoUsers.getById(f.getUser2()));
                } else if (f.getUser2().equals(id)) {
                    friends.add(repoUsers.getById(f.getUser1()));
                }
            }

            return friends;
        }
        /**
         *
         * DFS Algorithm that keeps the components it finds
         * @param user - a User object
         * @param visited - the users that have been visited in the algorithm
         * @param component - the community that is formed by running dfs
         */
        private void DFS(User user, Set<User> visited, List<User> component) {
            visited.add(user);
            component.add(user);

            for (User friend : getFriendsOf(user)) {
                if (!visited.contains(friend)) {
                    DFS(friend, visited, component);
                }
            }
        }

        @Override
        public int getNumberOfCommunities() {
            List<User> users = repoUsers.getAll();
            Set<User> visited = new HashSet<>();
            int result = 0;

            for (User u : users) {
                if (!visited.contains(u)) {
                    List<User> component = new ArrayList<>();
                    DFS(u, visited, component);
                    result++;
                }
            }

            return result;
        }

        /**
         * Gets all the communities
         * @return all the communities
         */
        private List<List<User>> getCommunities() {
            List<User> users = repoUsers.getAll();
            Set<User> visited = new HashSet<>();
            List<List<User>> communities = new ArrayList<>();

            for (User u : users) {
                if (!visited.contains(u)) {
                    List<User> component = new ArrayList<>();
                    DFS(u, visited, component);
                    communities.add(component);
                }
            }

            return communities;
        }

        /**
         * BFS Algorithm
         * @param start - a User object that is the starting point for bfs
         * @return - the ids of the users that result from the bfs
         */
        private Map<Long, Integer> BFS(User start) {
            Map<Long, Integer> dist = new HashMap<>();
            Queue<User> queue = new LinkedList<>();

            dist.put(start.getId(), 0);
            queue.add(start);

            while (!queue.isEmpty()) {
                User current = queue.poll();
                int currentDist = dist.get(current.getId());

                for (User friend : getFriendsOf(current)) {
                    if (!dist.containsKey(friend.getId())) {
                        dist.put(friend.getId(), currentDist + 1);
                        queue.add(friend);
                    }
                }
            }

            return dist;
        }

        private int communityDiameter(List<User> comunitate) {
            int dimensiune = 0;

            for (User u : comunitate) {
                Map<Long, Integer> dist = BFS(u);
                int localMax = dist.values().stream()
                        .max(Integer::compareTo)
                        .orElse(0);
                dimensiune = Math.max(dimensiune, localMax);
            }

            return dimensiune;
        }

        @Override
        public List<User> getMostSociableCommunity() {
            List<List<User>> communities = getCommunities();
            List<User> result = null;
            int dimensiuneMax = -1;

            for (List<User> c : communities) {
                int dimensiune = communityDiameter(c);

                if (dimensiune > dimensiuneMax) {
                    dimensiuneMax = dimensiune;
                    result = c;
                }
            }

            //System.out.println("Maximum diameter: " + dimensiuneMax);
            return result != null ? result : List.of();
        }


        @Override
        public User getUserById(Long id) {
            return repoUsers.getById(id);
        }


        ///----------------------------------------------------------------------------------------------------------------

        public void addFlock(String name, DuckType duckType, List<Long> members) {
            List<SwimmingDuck> swimmingDucks = new ArrayList<>();
            List<FlyingDuck> flyingDucks = new ArrayList<>();

            for (var id : members) {
                User user = repoUsers.getById(id);
                if (user == null) {
                    throw new UserException("Duck with id " + id + " not found.");
                }
                if (user instanceof Person) {
                    throw new UserException("User with id " + id + " is a person, not a duck.");
                }
                if ((user instanceof SwimmingDuck && duckType == DuckType.FLYING) ||
                        (user instanceof FlyingDuck && duckType == DuckType.SWIMMING)) {
                    throw new UserException("Invalid duck type for this flock!");
                }

                // Adaugam in lista corecta
                if (user instanceof SwimmingDuck swimmingDuck) {
                    swimmingDucks.add(swimmingDuck);
                } else if (user instanceof FlyingDuck flyingDuck) {
                    flyingDucks.add(flyingDuck);
                }
            }

            if (duckType == DuckType.FLYING) {
                Flock<FlyingDuck> flock = new FlyingFlock(name, DuckType.FLYING, flyingDucks);
                repoFlocks.add(flock);
            } else {
                Flock<SwimmingDuck> flock = new SwimmingFlock(name, DuckType.SWIMMING, swimmingDucks);
                repoFlocks.add(flock);
            }
        }

        @Override
        public List<Flock<? extends Duck>> getAllFlocks(){
            return repoFlocks.getAll();
        }

        @Override
        public void addRaceEvent(String name, List<Long> participants) {
            List<SwimmingDuck> swimmingDucks = new ArrayList<>();

            for (var id : participants) {
                User user = repoUsers.getById(id);
                if (user == null) {
                    throw new UserException("Duck with id " + id + " not found.");
                }
                if (user instanceof Person) {
                    throw new UserException("User with id " + id + " is a person, not a duck.");
                }
                if (user instanceof FlyingDuck) {
                    throw new UserException("Invalid duck type for the duck with id!" + id);
                }

                swimmingDucks.add((SwimmingDuck) user);
            }

            Event event = new RaceEvent(name, swimmingDucks);
            repoEvents.add(event);
        }

        public List<Event> getAllEvents(){
            return repoEvents.getAll();
        }

        @Override
        public List<SwimmingDuck> selectParticipants(Long id, int m) {
           // return ((EventRepo<Event>) repoEvents).selectParticipants(id, m);
            Event event = repoEvents.getById(id);

            if (event == null)
                throw new RuntimeException("Event not found!");

            if (!(event instanceof RaceEvent race))
                throw new RuntimeException("Event is not a race event!");

            return race.selectParticipants(m);
        }

        @Override
        public AbstractMap.SimpleEntry<Double,Double> getAveragePerformance(Long id){
            return ((DBFlockRepo) repoFlocks).getAveragePerformance(id);

        }

        // -------------------- DUCKS --------------------
        public Page<User> findAllDucksOnPage(Pageable pageable) {
            return repoUsers.findAllOnPage(pageable); // DUCK default
        }

        public Page<User> findAllDucksOnPage(Pageable pageable, DuckFilterDTO filter) {
            return repoUsers.findAllOnPage(pageable, filter);
        }

        // -------------------- PERSONS --------------------
        public Page<User> findAllPersonsOnPage(Pageable pageable) {
            return repoUsers.findAllPersonsOnPage(pageable); // PERSON default
        }

        public Page<User> findAllPersonsOnPage(Pageable pageable, PersonFilterDTO filter) {
            return repoUsers.findAllOnPage(pageable, filter);
        }

        public Page<Friendship> findAllFriendshipsOnPage(Pageable pageable) {
            return repoFriendships.findAllOnPage(pageable);
        }


        //--
        public Pair<User,Boolean> getUserByEmailPassword(String email,String password) throws SQLException {
            return repoUsers.getUserByEmailPassword(email,password);
        }

        public void addMessage(Message message){
            for ( Long idReceiver : message.getReceivers() ) {
                try{
                    repoUsers.getById(idReceiver);
                }
                catch (UserException e){
                    throw new UserException(e.getMessage());
                }
            }
            repoMessages.add(message);
        }

        public ObservableList<Message> getMessagesBetweenUsers(Long user1, Long user2) {
            List<Message> messageList = ((DBMessageRepo)repoMessages).getMessagesBetweenUsers(user1, user2);
            return FXCollections.observableArrayList(messageList);
        }

    }
